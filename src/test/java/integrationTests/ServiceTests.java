package integrationTests;

import helpers.IntegrationTest;
import roteService.orderBook.OrderBookSide;
import roteShared.service.TradingEngineServiceConsts;
import roteShared.service.TradingEngineServiceRequest;
import roteService.tradingEngine.LimitOrderResultStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests extends IntegrationTest {
    public ServiceTests() {
    }

    @Test
    public void orderShouldReject() throws Exception {
        var request = TradingEngineServiceRequest.limitOrder(10, 100, 1, "SPY", OrderBookSide.Buy);
        var response = testClient.send(TradingEngineServiceConsts.RequestTopic, "123", request);

        assertNotNull(response.limitOrderResult());
        assertEquals(LimitOrderResultStatus.Rejected, response.limitOrderResult().type());
    }

    @Test
    public void adjustBalance() throws Exception {
        send(TradingEngineServiceRequest.adjustBalance(10, 1, "USD"));
        var response = send(TradingEngineServiceRequest.getBalance(1, "USD"));
        assertEquals(10, response.getBalanceResult().balance());
    }

    @Test
    public void placeOrder() throws Exception {
        send(TradingEngineServiceRequest.adjustBalance(1000, 1, "USD"));
        var response = send(TradingEngineServiceRequest.limitOrder(1, 100, 1, "SPY", OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Ok, response.limitOrderResult().type());
    }

    @Test
    public void cancelOrder() throws Exception {
        send(TradingEngineServiceRequest.adjustBalance(1000, 1, "USD"));
        var response = send(TradingEngineServiceRequest.limitOrder(1, 100, 1, "SPY", OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Ok, response.limitOrderResult().type());
        var orderId = response.limitOrderResult().result().restingOrder().id();
        response = send(TradingEngineServiceRequest.getBalance(1, "USD"));
        assertEquals(900, response.getBalanceResult().balance());
        response = send(TradingEngineServiceRequest.cancel(1, "SPY", orderId));
        assertTrue(response.cancelOrderResult().success());
        response = send(TradingEngineServiceRequest.getBalance(1, "USD"));
        assertEquals(1000, response.getBalanceResult().balance());
    }

    @Test
    public void cancelOrderNotExists() throws Exception {
        var response = send(TradingEngineServiceRequest.cancel(1, "SPY", 0));
        assertFalse(response.cancelOrderResult().success());
    }

    @Test
    public void executeTrade() throws Exception {
        send(TradingEngineServiceRequest.adjustBalance(1000, 1, "USD"));
        send(TradingEngineServiceRequest.adjustBalance(1000, 2, "USD"));
        var response = send(TradingEngineServiceRequest.limitOrder(1, 100, 1, "SPY", OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Ok, response.limitOrderResult().type());
        response = send(TradingEngineServiceRequest.limitOrder(1, 99, 2, "SPY", OrderBookSide.Sell));
        assertEquals(LimitOrderResultStatus.Ok, response.limitOrderResult().type());
        assertEquals(1, response.limitOrderResult().result().trades().size());
    }
}
