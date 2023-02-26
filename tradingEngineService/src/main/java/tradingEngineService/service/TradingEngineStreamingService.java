package tradingEngineService.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import shared.kafka.RoteKafkaAdminClient;
import shared.kafka.RoteKafkaConsumer;
import shared.kafka.KafkaConsts;
import shared.kafka.RoteKafkaProducer;
import shared.orderBook.LimitOrderResultStatus;
import shared.service.TradingEngineServiceConsts;
import shared.service.TradingEngineServiceRequest;
import shared.service.TradingEngineServiceRequestType;
import shared.service.TradingEngineServiceResponse;
import shared.service.results.*;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@Component
public class TradingEngineStreamingService implements Runnable, Closeable {
    private static final Logger log = Logger.getLogger(TradingEngineStreamingService.class);

    private final RoteKafkaConsumer consumer;
    private final RoteKafkaProducer<String, TradingEngineServiceResponse> kafkaProducer;
    private final RoteKafkaProducer<String, OrderBookSnapshot> marketDataProducer;
    private final RoteKafkaAdminClient kafkaAdminClient;
    private final TradingEngine tradingEngine;
    private final ReferentialInventory referentialInventory;
    private final TradingEngineContextInstance tradingEngineContextInstance;
    private final ITradingEngineContextPersistor tradingContextPersistor;
    private final HashMap<TradingEngineServiceRequestType, ITradingEngineRequestHandler> handlers = new HashMap<>();

    public TradingEngineStreamingService(RoteKafkaConsumer requestConsumer,
                                         RoteKafkaProducer<String, TradingEngineServiceResponse> responseProducer,
                                         RoteKafkaProducer<String, OrderBookSnapshot> marketDataProducer,
                                         RoteKafkaAdminClient kafkaAdminClient, TradingEngine tradingEngine,
                                         ReferentialInventory referentialInventory,
                                         TradingEngineContextInstance tradingEngineContextInstance,
                                         ITradingEngineContextPersistor tradingContextPersistor) {
        this.consumer = requestConsumer;
        this.kafkaProducer = responseProducer;
        this.marketDataProducer = marketDataProducer;
        this.kafkaAdminClient = kafkaAdminClient;
        this.tradingEngine = tradingEngine;
        this.referentialInventory = referentialInventory;
        this.tradingEngineContextInstance = tradingEngineContextInstance;
        this.tradingContextPersistor = tradingContextPersistor;

        handlers.put(TradingEngineServiceRequestType.GetBalance, this::handleGetBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.GetBalances, this::handleGetBalancesRequest);
        handlers.put(TradingEngineServiceRequestType.GetBook, this::handleGetBookRequest);
        handlers.put(TradingEngineServiceRequestType.LimitOrder, this::handleLimitOrderRequest);
        handlers.put(TradingEngineServiceRequestType.AdjustBalance, this::handleAdjustBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.Cancel, this::handleCancelRequest);
        handlers.put(TradingEngineServiceRequestType.Error, this::handleErrorRequest);
    }

    public void run() {
        try {
            kafkaAdminClient.createTopic(TradingEngineServiceConsts.WriteRequestTopic, 1);
            kafkaAdminClient.createTopic(TradingEngineServiceConsts.ReadRequestTopic, 1);
            kafkaAdminClient.createTopic(TradingEngineServiceConsts.MarketDataTopic, 1);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        var startingOffset = tradingEngineContextInstance.getContext().sequence;
        try {
            log.info("Running trading engine service; starting at offset '" + startingOffset + "'");

            var topics = List.of(
                    new RoteKafkaConsumer.TopicPartitionAndOffet(TradingEngineServiceConsts.WriteRequestTopic, 0, Optional.of(startingOffset)),
                    new RoteKafkaConsumer.TopicPartitionAndOffet(TradingEngineServiceConsts.ReadRequestTopic, 0, Optional.empty())
                    );

            consumer.consumePartitions(topics, true, this::handleKafkaRecord, this::handleControlMessage);
            log.info("Stopped trading engine service");
        } catch (Exception e) {
            log.error("Trading engine service quit", e);
            throw e;
        }
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

    private TradingEngineServiceResponse handleGetBookRequest(TradingEngineServiceRequest request) throws Exception {
        var instrument = referentialInventory.lookupInstrumentOrThrow(request.instrumentCode());
        var book = getOrderBookSnapshot(instrument);
        return new TradingEngineServiceResponse(book);
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

    private TradingEngineServiceResponse handleLimitOrderRequest(TradingEngineServiceRequest request) throws Exception {
        var instrument = referentialInventory.lookupInstrumentOrThrow(request.instrumentCode());
        var limitOrder = new LimitOrder(instrument, new Account(request.accountId()), request.amount(), request.price(), request.side());
        var limitOrderResult = tradingEngine.limitOrder(limitOrder);
        if (limitOrderResult.type() == LimitOrderResultStatus.Ok) sendOrderBookSnapshotUpdate(instrument);
        return new TradingEngineServiceResponse(limitOrderResult);
    }

    private void sendOrderBookSnapshotUpdate(Instrument instrument) {
        var book = getOrderBookSnapshot(instrument);
        marketDataProducer.produce(TradingEngineServiceConsts.MarketDataTopic, instrument.code(), book, null, true);
    }

    private OrderBookSnapshot getOrderBookSnapshot(Instrument instrument) {
        var orderBook = tradingEngineContextInstance.getContext().ensureOrderBook(instrument);
        return new OrderBookSnapshot(instrument.code(), orderBook.orderBook().bids, orderBook.orderBook().asks);
    }

    private TradingEngineServiceResponse handleCancelRequest(TradingEngineServiceRequest request) throws Exception {
        var instrument = referentialInventory.lookupInstrumentOrThrow(request.instrumentCode());
        var isCancelled = tradingEngine.cancel(new Account(request.accountId()), instrument, request.orderId());
        if (isCancelled) sendOrderBookSnapshotUpdate(instrument);
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
