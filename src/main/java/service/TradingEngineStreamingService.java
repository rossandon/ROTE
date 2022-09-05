package service;

import kafka.KafkaClient;
import kafka.KafkaConsts;
import orderBook.OrderBookSide;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import referential.InstrumentInventory;
import tradingEngine.Account;
import tradingEngine.LimitOrder;
import tradingEngine.TradingEngine;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

public class TradingEngineStreamingService implements Runnable {

    private final KafkaClient client;
    private final TradingEngine tradingEngine;
    private final InstrumentInventory instrumentInventory;

    public TradingEngineStreamingService(KafkaClient client, TradingEngine tradingEngine, InstrumentInventory instrumentInventory) {
        this.client = client;
        this.tradingEngine = tradingEngine;
        this.instrumentInventory = instrumentInventory;
    }

    public void run() {
        client.consume(Collections.singletonList(TradingEngineServiceConsts.RequestTopic), this::handle);
    }

    private void handle(ConsumerRecord<String, TradingEngineServiceRequest> record) {
        var request = record.value();
        var responseTopicBytes = record.headers().headers(KafkaConsts.ResponseTopicHeader).iterator().next().value();
        var responseIdBytes = record.headers().headers(KafkaConsts.ResponseIdHeader).iterator().next().value();
        var responseTopic = new String(responseTopicBytes, StandardCharsets.UTF_8);
        var responseId = new String(responseIdBytes, StandardCharsets.UTF_8);
        if (request.type() == TradingEngineServiceRequestType.LimitOrder) {
            var response = handleOrderRequest(request.order());
            sendResponse(responseTopic, responseId, response);
        }
    }

    private void sendResponse(String responseTopic, String responseId, TradingEngineServiceResponse response) {
        Header responseIdHeader = new KafkaClient.KafkaHeader(KafkaConsts.ResponseIdHeader, responseId.getBytes(StandardCharsets.UTF_8));
        var headers = Arrays.asList(responseIdHeader);
        client.produce(responseTopic, null, response, headers, false);
    }

    private TradingEngineServiceResponse handleOrderRequest(TradingEngineServiceOrder order) {
        var instrument = instrumentInventory.lookupInstrument(order.instrumentCode());
        var limitOrder = new LimitOrder(instrument, new Account(order.accountId()), order.size(), order.price(), OrderBookSide.Buy);
        var limitOrderResult = tradingEngine.processOrder(limitOrder);
        return new TradingEngineServiceResponse(limitOrderResult);
    }
}
