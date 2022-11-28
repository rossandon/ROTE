package ROTE.service;

import ROTE.tradingEngine.Account;
import ROTE.tradingEngine.LimitOrder;
import ROTE.tradingEngine.TradingEngine;
import ROTE.kafka.KafkaClient;
import ROTE.kafka.KafkaConsts;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import ROTE.referential.ReferentialInventory;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Component
public class TradingEngineStreamingService implements Runnable, Closeable {
    private static final Logger log = Logger.getLogger(TradingEngineStreamingService.class);
    private final KafkaClient<String, TradingEngineServiceResponse> client;
    private final TradingEngine tradingEngine;
    private final ReferentialInventory referentialInventory;
    private final ITradingEngineContextPersistor tradingContextPersistor;
    private final HashMap<TradingEngineServiceRequestType, ITradingEngineRequestHandler> handlers = new HashMap<>();

    public TradingEngineStreamingService(KafkaClient<String, TradingEngineServiceResponse> client, TradingEngine tradingEngine, ReferentialInventory referentialInventory, ITradingEngineContextPersistor tradingContextPersistor) {
        this.client = client;
        this.tradingEngine = tradingEngine;
        this.referentialInventory = referentialInventory;
        this.tradingContextPersistor = tradingContextPersistor;

        handlers.put(TradingEngineServiceRequestType.GetBalance, this::handleGetBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.LimitOrder, this::handleLimitOrderRequest);
        handlers.put(TradingEngineServiceRequestType.AdjustBalance, this::handleAdjustBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.Cancel, this::handleCancelRequest);
        handlers.put(TradingEngineServiceRequestType.Snapshot, this::handleSnapshot);
    }

    public void run() {
        log.info("Running trading engine service");
        client.consume(Collections.singletonList(TradingEngineServiceConsts.RequestTopic), this::handle);
        log.info("Stopped trading engine service");
    }

    private void handle(ConsumerRecord<String, TradingEngineServiceRequest> record) {
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

    private TradingEngineServiceResponse handleSnapshot(TradingEngineServiceRequest request) {
        var context = tradingEngine.getContext();
        tradingContextPersistor.save(context);
        return new TradingEngineServiceResponse();
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
