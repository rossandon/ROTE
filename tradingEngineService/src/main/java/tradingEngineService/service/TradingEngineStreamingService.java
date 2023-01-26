package tradingEngineService.service;

import shared.kafka.KafkaClient;
import shared.kafka.KafkaConsts;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
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
        handlers.put(TradingEngineServiceRequestType.GetBalances, this::handleGetBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.LimitOrder, this::handleLimitOrderRequest);
        handlers.put(TradingEngineServiceRequestType.AdjustBalance, this::handleAdjustBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.Cancel, this::handleCancelRequest);
    }

    public void run() {
        log.info("Running trading engine service");
        client.consume(Collections.singletonList(TradingEngineServiceConsts.RequestTopic), this::handleKafkaRecord, this::handleControlMessage);
        log.info("Stopped trading engine service");
    }

    public Future snapshot() {
        return client.queueControlMessage(new TradingEngineServiceSnapshotRequest());
    }

    private void handleControlMessage(ProcessingQueue.ProcessingQueueItem o) {
        try {
            if (o.getObject() instanceof TradingEngineServiceSnapshotRequest) {
                var context = tradingEngineContextInstance.getContext();
                tradingContextPersistor.save(context);
                o.setResult(true);
            } else
                throw new Exception("Unknown control message type");
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

        var handler = handlers.get(request.type());
        var response = handler.handle(request);
        sendResponse(responseTopic, responseId, response);
    }

    private TradingEngineServiceResponse handleGetBalanceRequest(TradingEngineServiceRequest request) {
        var asset = referentialInventory.lookupAsset(request.assetCode());
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

    private TradingEngineServiceResponse handleAdjustBalanceRequest(TradingEngineServiceRequest request) {
        var asset = referentialInventory.lookupAsset(request.assetCode());
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
