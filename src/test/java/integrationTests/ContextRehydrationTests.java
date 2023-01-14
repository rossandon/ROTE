package integrationTests;

import ROTE.service.TradingEngineServiceConsts;
import ROTE.service.TradingEngineServiceRequest;
import ROTE.tradingEngine.TradingEngineContext;
import helpers.AwsTradingEngineContextSeeder;
import helpers.IntegrationTest;
import helpers.TestHelpers;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(initializers = ContextRehydrationTests.ContextSeeder.class)
public class ContextRehydrationTests extends IntegrationTest {

    @Test
    public void Test1() throws Exception {
        var request = TradingEngineServiceRequest.getBalance(TestHelpers.testAccount1.accountId(), TestHelpers.USD.code());
        var response = testClient.send(TradingEngineServiceConsts.RequestTopic, "123", request);
        assertEquals(100, response.getBalanceResult().balance());
    }

    public static class ContextSeeder extends AwsTradingEngineContextSeeder {
        @Override
        protected void seedContext(TradingEngineContext tradingEngineContext) {
            tradingEngineContext.adjustBalance(TestHelpers.testAccount1.accountId(), TestHelpers.USD, 100);
        }
    }
}
