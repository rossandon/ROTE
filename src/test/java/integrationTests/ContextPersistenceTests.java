package integrationTests;

import ROTE.service.TradingEngineServiceRequest;
import ROTE.service.TradingEngineStreamingService;
import ROTE.tradingEngine.TradingEngineContextInstance;
import helpers.AwsTradingEngineContextSeeder;
import helpers.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(initializers = AwsTradingEngineContextSeeder.class)
public class ContextPersistenceTests extends IntegrationTest {

    @Autowired
    TradingEngineContextInstance instance;

    @Autowired
    TradingEngineStreamingService tradingEngineStreamingService;

    @Test
    public void Test1() throws Exception {
        var instance1 = instance.getContext();
        send(TradingEngineServiceRequest.adjustBalance(20, 1, "USD"));

        var future = tradingEngineStreamingService.snapshot();
        var result = future.get();
        assertTrue((Boolean)result);

        instance.reload();
        var instance2 = instance.getContext();
        assertTrue(instance1 != instance2);

        var response = send(TradingEngineServiceRequest.getBalance(1, "USD"));
        assertEquals(20, response.getBalanceResult().balance());
    }
}
