package service;

import kafka.KafkaClient;
import kafka.KafkaConsts;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.log4j.Logger;
import referential.Asset;
import referential.InstrumentInventory;
import tradingEngine.Account;
import tradingEngine.LimitOrder;
import tradingEngine.TradingEngine;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TradingEngineStreamingService implements Runnable {
    private static final Logger log = Logger.getLogger(TradingEngineStreamingService.class);
    private final KafkaClient client;
    private final TradingEngine tradingEngine;
    private final InstrumentInventory instrumentInventory;
    private final HashMap<TradingEngineServiceRequestType, ITradingEngineRequestHandler> handlers = new HashMap<>();

    public TradingEngineStreamingService(KafkaClient client, TradingEngine tradingEngine, InstrumentInventory instrumentInventory) {
        this.client = client;
        this.tradingEngine = tradingEngine;
        this.instrumentInventory = instrumentInventory;

        handlers.put(TradingEngineServiceRequestType.GetBalance, this::handleGetBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.LimitOrder, this::handleLimitOrderRequest);
        handlers.put(TradingEngineServiceRequestType.AdjustBalance, this::handleAdjustBalanceRequest);
        handlers.put(TradingEngineServiceRequestType.Cancel, this::handleCancelRequest);
    }

    public void run() {
        log.info("Running trading engine service");
        client.consume(Collections.singletonList(TradingEngineServiceConsts.RequestTopic), this::handle);
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
        var balance = tradingEngine.getBalance(request.accountId(), new Asset(request.assetCode()));
        return new TradingEngineServiceResponse(new GetBalanceResult(balance));
    }

    private TradingEngineServiceResponse handleAdjustBalanceRequest(TradingEngineServiceRequest request) {
        tradingEngine.adjustBalance(new Account(request.accountId()), new Asset(request.assetCode()), request.amount());
        return new TradingEngineServiceResponse();
    }

    private TradingEngineServiceResponse handleLimitOrderRequest(TradingEngineServiceRequest request) {
        var instrument = instrumentInventory.lookupInstrument(request.instrumentCode());
        var limitOrder = new LimitOrder(instrument, new Account(request.accountId()), request.amount(), request.price(), request.side());
        var limitOrderResult = tradingEngine.limitOrder(limitOrder);
        return new TradingEngineServiceResponse(limitOrderResult);
    }

    private TradingEngineServiceResponse handleCancelRequest(TradingEngineServiceRequest request) {
        var instrument = instrumentInventory.lookupInstrument(request.instrumentCode());
        var isCancelled = tradingEngine.cancel(new Account(request.accountId()), instrument, request.orderId());
        return new TradingEngineServiceResponse(new CancelOrderResult(isCancelled));
    }

    private void sendResponse(String responseTopic, String responseId, TradingEngineServiceResponse response) {
        Header responseIdHeader = new KafkaClient.KafkaHeader(KafkaConsts.ResponseIdHeader, responseId.getBytes(StandardCharsets.UTF_8));
        var headers = List.of(responseIdHeader);
        client.produce(responseTopic, null, response, headers, false);
    }
}
