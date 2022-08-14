import orderBook.OrderBookLimitOrder;
import orderBook.OrderBook;
import orderBook.OrderBookLimitOrderResult;
import orderBook.OrderBookSide;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {
    @Test
    public void limitOrderScenarios() {
        var book = new OrderBook();
        book.process(new OrderBookLimitOrder(100, 100, OrderBookSide.Buy, 1));
        book.process(new OrderBookLimitOrder(100, 99, OrderBookSide.Buy, 1));

        assertEquals(2, book.Bids.size());
        assertEquals(100, book.Bids.get(0).price());
        assertEquals(99, book.Bids.get(1).price());

        book.process(new OrderBookLimitOrder(49, 100, OrderBookSide.Sell,1));
        book.process(new OrderBookLimitOrder(10, 101, OrderBookSide.Sell, 1));

        assertEquals(2, book.Bids.size());
        assertEquals(100, book.Bids.get(0).price());
        assertEquals(51, book.Bids.get(0).size());

        assertEquals(1, book.Asks.size());
    }

    @Test
    public void fundingCheck() {
        var tradingEngine = new TradingEngine(new TradingEngineContext());
        tradingEngine.addFunding(TestHelpers.testAccount, TestHelpers.USD, 100);

        var result = tradingEngine.process(new LimitOrder(TestHelpers.SPYUSD, TestHelpers.testAccount, 10, 11, OrderBookSide.Buy));
        assertEquals(LimitOrderResultType.Rejected, result.type());
        result = tradingEngine.process(new LimitOrder(TestHelpers.SPYUSD, TestHelpers.testAccount, 10, 9, OrderBookSide.Buy));
        assertEquals(LimitOrderResultType.Ok, result.type());
    }
}
