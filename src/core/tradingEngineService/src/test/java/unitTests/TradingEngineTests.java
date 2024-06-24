package unitTests;

import shared.orderBook.LimitOrderResultStatus;
import helpers.TestHelpers;
import shared.orderBook.OrderBookSide;
import org.junit.jupiter.api.Test;
import tradingEngineService.tradingEngine.LimitOrder;
import tradingEngineService.tradingEngine.TradingEngine;
import tradingEngineService.tradingEngine.TradingEngineContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradingEngineTests {
    @Test
    public void fundingCheckShouldReject() {
        var tradingEngine = new TradingEngine(new TradingEngineContext());
        tradingEngine.adjustBalance(TestHelpers.testAccount1, TestHelpers.USD, BigDecimal.valueOf(100));

        var result = tradingEngine.limitOrder(new LimitOrder(TestHelpers.SPYInst, TestHelpers.testAccount1, BigDecimal.valueOf(10), BigDecimal.valueOf(11), OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Rejected, result.type());
        result = tradingEngine.limitOrder(new LimitOrder(TestHelpers.SPYInst, TestHelpers.testAccount1, BigDecimal.valueOf(10), BigDecimal.valueOf(9), OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Ok, result.type());
        var usdBalance = tradingEngine.getBalance(TestHelpers.testAccount1.accountId(), TestHelpers.USD);
        var spyBalance = tradingEngine.getBalance(TestHelpers.testAccount1.accountId(), TestHelpers.SPY);
        assertEquals(10, usdBalance.longValue());
    }

    @Test
    public void filledTrade() {
        var tradingEngine = new TradingEngine(new TradingEngineContext());
        tradingEngine.adjustBalance(TestHelpers.testAccount1, TestHelpers.USD, BigDecimal.valueOf(200));
        tradingEngine.adjustBalance(TestHelpers.testAccount2, TestHelpers.SPY, BigDecimal.valueOf(10));

        var result = tradingEngine.limitOrder(new LimitOrder(TestHelpers.SPYInst, TestHelpers.testAccount2, BigDecimal.valueOf(10), BigDecimal.valueOf(10), OrderBookSide.Sell));
        assertEquals(LimitOrderResultStatus.Ok, result.type());

        result = tradingEngine.limitOrder(new LimitOrder(TestHelpers.SPYInst, TestHelpers.testAccount1, BigDecimal.valueOf(10), BigDecimal.valueOf(11), OrderBookSide.Buy));
        assertEquals(LimitOrderResultStatus.Ok, result.type());
        assertEquals(1, result.result().trades().size());
        assertEquals(BigDecimal.valueOf(10), result.result().trades().get(0).price());
        var usdBalance = tradingEngine.getBalance(TestHelpers.testAccount1.accountId(), TestHelpers.USD);
        assertEquals(BigDecimal.valueOf(100), usdBalance);
    }
}
