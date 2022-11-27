package unitTests;

import helpers.TestHelpers;
import ROTE.orderBook.OrderBookSide;
import org.junit.jupiter.api.Test;
import ROTE.tradingEngine.LimitOrder;
import ROTE.tradingEngine.LimitOrderResultStatus;
import ROTE.tradingEngine.TradingEngine;
import ROTE.tradingEngine.TradingEngineContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradingEngineTests {
    @Test
    public void fundingCheckShouldReject() {
        var tradingEngine = new TradingEngine(new TradingEngineContext());
        tradingEngine.adjustBalance(TestHelpers.testAccount1, TestHelpers.USD, 100);

        var result = tradingEngine.limitOrder(new LimitOrder(TestHelpers.SPYInst, TestHelpers.testAccount1, 10, 11, OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Rejected, result.type());
        result = tradingEngine.limitOrder(new LimitOrder(TestHelpers.SPYInst, TestHelpers.testAccount1, 10, 9, OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Ok, result.type());
        var usdBalance = tradingEngine.getBalance(TestHelpers.testAccount1.accountId(), TestHelpers.USD);
        var spyBalance = tradingEngine.getBalance(TestHelpers.testAccount1.accountId(), TestHelpers.SPY);
        assertEquals(10, usdBalance);
    }
}
