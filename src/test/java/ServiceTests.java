import kafka.KafkaClient;
import kafka.KafkaRequestResponseClient;
import orderBook.OrderBookSide;
import org.junit.jupiter.api.Test;
import service.*;
import tradingEngine.LimitOrderResultStatus;
import tradingEngine.TradingEngine;
import tradingEngine.TradingEngineContext;
import Utils.UuidHelper;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests implements AutoCloseable {
    private final String namespace = UuidHelper.GetNewUuid();
    private final TradingEngine tradingEngine = new TradingEngine(new TradingEngineContext());
    private final KafkaRequestResponseClient<String, TradingEngineServiceRequest, TradingEngineServiceResponse> testClient = TestHelpers.getKafkaRequestResponseClient("test", namespace);
    private final KafkaClient engineClient = TestHelpers.getKafkaClient("engine", namespace);
    private final TradingEngineStreamingService tradingEngineStreamingService = new TradingEngineStreamingService(engineClient, tradingEngine, TestHelpers.GetInventory());

    public ServiceTests() {
        var tradingEngineThread = new Thread(tradingEngineStreamingService);
        tradingEngineThread.start();
        var testClientThread = new Thread(testClient);
        testClientThread.start();
    }

    @Test
    public void orderShouldReject() throws Exception {
        var request = new TradingEngineServiceRequest(10, 100, 1, "SPY", OrderBookSide.Buy);
        var response = testClient.send(TradingEngineServiceConsts.RequestTopic, "123", request);

        assertNotNull(response.limitOrderResult());
        assertEquals(LimitOrderResultStatus.Rejected, response.limitOrderResult().type());
    }

    @Test
    public void adjustBalance() throws Exception {
        send(new TradingEngineServiceRequest(10, 1, "USD"));
        var response = send(new TradingEngineServiceRequest(1, "USD"));
        assertEquals(10, response.result().balance());
    }

    @Test
    public void placeOrder() throws Exception {
        send(new TradingEngineServiceRequest(1000, 1, "USD"));
        var response = send(new TradingEngineServiceRequest(1, 100, 1, "SPY", OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Ok, response.limitOrderResult().type());
    }

    @Test
    public void executeTrade() throws Exception {
        send(new TradingEngineServiceRequest(1000, 1, "USD"));
        send(new TradingEngineServiceRequest(1000, 2, "USD"));
        var response = send(new TradingEngineServiceRequest(1, 100, 1, "SPY", OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Ok, response.limitOrderResult().type());
        response = send(new TradingEngineServiceRequest(1, 99, 2, "SPY", OrderBookSide.Sell));
        assertEquals(LimitOrderResultStatus.Ok, response.limitOrderResult().type());
        assertEquals(1, response.limitOrderResult().result().trades().size());
    }

    private TradingEngineServiceResponse send(TradingEngineServiceRequest request) throws Exception {
        return testClient.send(TradingEngineServiceConsts.RequestTopic, "", request);
    }

    @Override
    public void close() throws Exception {
        testClient.close();
        engineClient.close();
    }
}
