package tradingEngineService.service;

import shared.kafka.KafkaClient;
import shared.kafka.KafkaConsts;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import shared.service.results.CancelOrderResult;
import shared.service.results.GetBalanceResult;
import shared.service.results.GetBalancesResult;
import shared.service.results.TradingEngineErrorResult;
import tradingEngineService.referential.ReferentialInventory;
import shared.utils.ProcessingQueue;
import shared.service.*;
import tradingEngineService.tradingEngine.Account;
import tradingEngineService.tradingEngine.LimitOrder;
import tradingEngineService.tradingEngine.TradingEngine;
import tradingEngineService.tradingEngine.TradingEngineContextInstance;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

@Component
public class TradingEngineStreamingService implements Runnable, Closeable {
    private static final Logger log = Logger.getLogger(TradingEngineStreamingService.class);
    private final KafkaClient<String, TradingEngineServiceResponse> client;
    private final TradingEngine tradingEngine;
    private final ReferentialInventory referentialInventory;
    private final TradingEngineContextInstance tradingEngineContextInstance;
    private final ITradingEngineContextPersistor tradingContextPersistor;
    private final HashMap<TradingEngineServiceRequestType, ITradingEngineRequestHandler> handlers = new HashMap<>();

    public TradingEngineStreamingService(KafkaClient<String, TradingEngineServiceResponse> client,
                                         TradingEngine tradingEngine, ReferentialInventory referentialInventory,
                                         TradingEngineContextInstance tradingEngineContextInstance,
                                         ITradingEngineContextPersistor tradingContextPersistor) {
        this.client = client;
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
        client.consume(TradingEngineServiceConsts.RequestTopic, startingOffset, this::handleKafkaRecord, this::handleControlMessage);
        log.info("Stopped trading engine service");
    }

    public Future<Object> snapshot() {
        return client.queueControlMessage(new TradingEngineServiceSnapshotRequest());
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
        return new TradingEngineServiceResponse(limitOrderResult);
    }

    private TradingEngineServiceResponse handleCancelRequest(TradingEngineServiceRequest request) {
        var instrument = referentialInventory.lookupInstrument(request.instrumentCode());
        var isCancelled = tradingEngine.cancel(new Account(request.accountId()), instrument, request.orderId());
        return new TradingEngineServiceResponse(new CancelOrderResult(isCancelled));
    }

    private TradingEngineServiceResponse handleErrorRequest(TradingEngineServiceRequest request) throws Exception {
        throw new Exception("ping");
    }

    private void sendResponse(String responseTopic, String responseId, TradingEngineServiceResponse response) {
        Header responseIdHeader = new KafkaClient.KafkaHeader(KafkaConsts.ResponseIdHeader, responseId.getBytes(StandardCharsets.UTF_8));
        var headers = List.of(responseIdHeader);
        client.produce(responseTopic, null, response, headers, false);
    }

    @Override
    public void close() {
        client.close();
    }
}
