package tradingEngineService.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import shared.kafka.RoteKafkaConsumer;
import shared.kafka.KafkaConsts;
import shared.kafka.RoteKafkaProducer;
import shared.orderBook.LimitOrderResultStatus;
import shared.service.TradingEngineServiceConsts;
import shared.service.TradingEngineServiceRequest;
import shared.service.TradingEngineServiceRequestType;
import shared.service.TradingEngineServiceResponse;
import shared.service.results.CancelOrderResult;
import shared.service.results.GetBalanceResult;
import shared.service.results.GetBalancesResult;
import shared.service.results.TradingEngineErrorResult;
import shared.utils.ProcessingQueue;
import tradingEngineService.referential.Instrument;
import tradingEngineService.referential.ReferentialInventory;
import tradingEngineService.tradingEngine.Account;
import tradingEngineService.tradingEngine.LimitOrder;
import tradingEngineService.tradingEngine.TradingEngine;
import tradingEngineService.tradingEngine.TradingEngineContextInstance;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

@Component
public class TradingEngineStreamingService implements Runnable, Closeable {
    private static final Logger log = Logger.getLogger(TradingEngineStreamingService.class);

    private final RoteKafkaConsumer consumer;
    private final RoteKafkaProducer<String, TradingEngineServiceResponse> kafkaProducer;
    private final RoteKafkaProducer<String, MarketDataUpdate> marketDataProducer;
    private final TradingEngine tradingEngine;
    private final ReferentialInventory referentialInventory;
    private final TradingEngineContextInstance tradingEngineContextInstance;
    private final ITradingEngineContextPersistor tradingContextPersistor;
    private final HashMap<TradingEngineServiceRequestType, ITradingEngineRequestHandler> handlers = new HashMap<>();

    public TradingEngineStreamingService(RoteKafkaConsumer requestConsumer,
                                         RoteKafkaProducer<String, TradingEngineServiceResponse> responseProducer,
                                         RoteKafkaProducer<String, MarketDataUpdate> marketDataProducer,
                                         TradingEngine tradingEngine, ReferentialInventory referentialInventory,
                                         TradingEngineContextInstance tradingEngineContextInstance,
                                         ITradingEngineContextPersistor tradingContextPersistor) {
        this.consumer = requestConsumer;
        this.kafkaProducer = responseProducer;
        this.marketDataProducer = marketDataProducer;
        this.tradingEngine = tradingEngine;
        this.referentialInventory = referentialInventory;
        this.tradingEngineContextInstance = tradingEngineContextInstance;
        this.tradingContextPersistor = tradingContextPersistor;

        handlers.put(TradingEngineServiceRequestType.GetBalance, this::handleGetBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.GetBalances, this::handleGetBalancesRequest);
        handlers.put(TradingEngineServiceRequestType.LimitOrder, this::handleLimitOrderRequest);
        handlers.put(TradingEngineServiceRequestType.AdjustBalance, this::handleAdjustBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.Cancel, this::handleCancelRequest);
        handlers.put(TradingEngineServiceRequestType.Error, this::handleErrorRequest);
    }

    public void run() {
        var startingOffset = tradingEngineContextInstance.getContext().sequence;
        log.info("Running trading engine service; starting at offset '" + startingOffset + "'");
        consumer.consume(TradingEngineServiceConsts.RequestTopic, startingOffset, false, this::handleKafkaRecord, this::handleControlMessage);
        log.info("Stopped trading engine service");
    }

    public Future<Object> snapshot() {
        return consumer.queueControlMessage(new TradingEngineServiceSnapshotRequest());
    }

    private void handleControlMessage(ProcessingQueue.ProcessingQueueItem o) {
        try {
            if (o.getObject() instanceof TradingEngineServiceSnapshotRequest) {
                var context = tradingEngineContextInstance.getContext();
                tradingContextPersistor.save(context);
                o.setResult(true);
            } else throw new Exception("Unknown control message type");
        } catch (Exception e) {
            log.error("Failed to process control message", e);
            o.setException(e);
        }
    }

    private void handleKafkaRecord(ConsumerRecord<String, TradingEngineServiceRequest> record) {
        var request = record.value();
        var responseTopicBytes = record.headers().headers(KafkaConsts.ResponseTopicHeader).iterator().next().value();
        var responseIdBytes = record.headers().headers(KafkaConsts.ResponseIdHeader).iterator().next().value();
        var responseTopic = new String(responseTopicBytes, StandardCharsets.UTF_8);
        var responseId = new String(responseIdBytes, StandardCharsets.UTF_8);

        TradingEngineServiceResponse response;
        try {
            var type = request.type();
            log.info("Processing '" + type + "' request");
            var handler = handlers.get(type);
            response = handler.handle(request);
            tradingEngineContextInstance.getContext().sequence = record.offset();
        } catch (Exception e) {
            log.error("Failed to process request", e);
            response = new TradingEngineServiceResponse(new TradingEngineErrorResult(e.getMessage()));
        }

        sendResponse(responseTopic, responseId, response);
    }

    private TradingEngineServiceResponse handleGetBalanceRequest(TradingEngineServiceRequest request) throws Exception {
        var asset = referentialInventory.lookupAssetOrThrow(request.assetCode());
        var balance = tradingEngine.getBalance(request.accountId(), asset);
        return new TradingEngineServiceResponse(new GetBalanceResult(balance));
    }

    private TradingEngineServiceResponse handleGetBalancesRequest(TradingEngineServiceRequest request) {
        var allAssets = referentialInventory.getAllAssets();
        var map = new HashMap<String, Long>();
        for (var asset : allAssets) {
            var balance = tradingEngine.getBalance(request.accountId(), asset);
            map.put(asset.code(), balance);
        }
        return new TradingEngineServiceResponse(new GetBalancesResult(map));
    }

    private TradingEngineServiceResponse handleAdjustBalanceRequest(
            TradingEngineServiceRequest request) throws Exception {
        var asset = referentialInventory.lookupAssetOrThrow(request.assetCode());
        tradingEngine.adjustBalance(new Account(request.accountId()), asset, request.amount());
        return new TradingEngineServiceResponse();
    }

    private TradingEngineServiceResponse handleLimitOrderRequest(TradingEngineServiceRequest request) {
        var instrument = referentialInventory.lookupInstrument(request.instrumentCode());
        var limitOrder = new LimitOrder(instrument, new Account(request.accountId()), request.amount(), request.price(), request.side());
        var limitOrderResult = tradingEngine.limitOrder(limitOrder);
        if (limitOrderResult.type() == LimitOrderResultStatus.Ok) sendMarketData(instrument);
        return new TradingEngineServiceResponse(limitOrderResult);
    }

    private void sendMarketData(Instrument instrument) {
        var orderBook = tradingEngineContextInstance.getContext().orderBooks.get(instrument.id()).orderBook();
        var marketDataUpdate = new MarketDataUpdate(instrument.code(), orderBook);
        marketDataProducer.produce(TradingEngineServiceConsts.MarketDataTopic, instrument.code(), marketDataUpdate, null, true);
    }

    private TradingEngineServiceResponse handleCancelRequest(TradingEngineServiceRequest request) {
        var instrument = referentialInventory.lookupInstrument(request.instrumentCode());
        var isCancelled = tradingEngine.cancel(new Account(request.accountId()), instrument, request.orderId());
        if (isCancelled)
            sendMarketData(instrument);
        return new TradingEngineServiceResponse(new CancelOrderResult(isCancelled));
    }

    private TradingEngineServiceResponse handleErrorRequest(TradingEngineServiceRequest request) throws Exception {
        throw new Exception("ping");
    }

    private void sendResponse(String responseTopic, String responseId, TradingEngineServiceResponse response) {
        Header responseIdHeader = new RoteKafkaConsumer.KafkaHeader(KafkaConsts.ResponseIdHeader, responseId.getBytes(StandardCharsets.UTF_8));
        var headers = List.of(responseIdHeader);
        kafkaProducer.produce(responseTopic, null, response, headers, true);
    }

    @Override
    public void close() {
        consumer.close();
        kafkaProducer.close();
        marketDataProducer.close();
    }
}
