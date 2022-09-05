import kafka.KafkaClient;
import kafka.KafkaRequestResponseClient;
import org.apache.kafka.common.security.oauthbearer.secured.AccessTokenRetriever;
import org.junit.jupiter.api.Test;
import service.*;
import tradingEngine.LimitOrderResultStatus;
import tradingEngine.TradingEngine;
import tradingEngine.TradingEngineContext;
import Utils.UuidHelper;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServiceTests {
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
        var request = new TradingEngineServiceRequest(TradingEngineServiceRequestType.LimitOrder, new TradingEngineServiceOrder(10, 100, 1, "SPY"));
        var response = testClient.send(TradingEngineServiceConsts.RequestTopic, "123", request);

        assertNotNull(response.limitOrderResult());
        assertEquals(LimitOrderResultStatus.Rejected, response.limitOrderResult().type());
    }
}
