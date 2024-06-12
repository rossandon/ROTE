package unitTests;

import tradingEngineService.orderBook.OrderBook;
import tradingEngineService.orderBook.OrderBookLimitOrder;
import shared.orderBook.OrderBookLimitOrderResultStatus;
import shared.orderBook.OrderBookSide;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderBookTests {
    @Test
    public void limitOrderShouldRest() {
        var book = new OrderBook();
        book.processOrder(new OrderBookLimitOrder(100, 100, OrderBookSide.Buy, 1));
        book.processOrder(new OrderBookLimitOrder(100, 99, OrderBookSide.Buy, 1));

        assertEquals(2, book.bids.size());
        assertEquals(100, book.bids.get(0).price().longValue());
        assertEquals(99, book.bids.get(1).price().longValue());

        var res1 = book.processOrder(new OrderBookLimitOrder(49, 100, OrderBookSide.Sell, 2));
        var res2 = book.processOrder(new OrderBookLimitOrder(10, 101, OrderBookSide.Sell, 2));

        assertEquals(OrderBookLimitOrderResultStatus.Filled, res1.status());
        assertEquals(1, res1.trades().size());
        assertEquals(2, res1.trades().get(0).takerAccountId());
        assertEquals(1, res1.trades().get(0).makerAccountId());
        assertEquals(OrderBookSide.Sell, res1.trades().get(0).takerSide());

        assertNull(res1.restingOrder());
        assertNotNull(res2.restingOrder());
        assertNull(res2.trades());

        assertEquals(2, book.bids.size());
        assertEquals(100, book.bids.get(0).price().longValue());
        assertEquals(51, book.bids.get(0).size().longValue());

        assertEquals(1, book.asks.size());
    }

    @Test
    public void filledOrdersShouldBeRemoved() {
        var book = new OrderBook();
        book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Sell, 0));
        book.processOrder(new OrderBookLimitOrder(11, 101, OrderBookSide.Sell, 0));
        book.processOrder(new OrderBookLimitOrder(12, 102, OrderBookSide.Sell, 0));
        assertEquals(3, book.asks.size());
        var res1 = book.processOrder(new OrderBookLimitOrder(40, 103, OrderBookSide.Buy, 1));
        assertEquals(3, res1.trades().size());
        assertEquals(100, res1.trades().get(0).price().longValue());
        assertEquals(101, res1.trades().get(1).price().longValue());
        assertEquals(102, res1.trades().get(2).price().longValue());
        assertEquals(0, book.asks.size());
    }

    @Test
    public void cancelledAskShouldBeRemoved() {
        var book = new OrderBook();
        var res1 = book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Sell, 0));
        book.processOrder(new OrderBookLimitOrder(10, 101, OrderBookSide.Sell, 0));
        assertEquals(2, book.asks.size());
        var cancelledOrder = book.cancelOrder(res1.restingOrder().id());
        assertEquals(1, book.asks.size());
        assertNotNull(cancelledOrder);
    }

    @Test
    public void cancelledBidShouldBeRemoved() {
        var book = new OrderBook();
        var res1 = book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Buy, 0));
        book.processOrder(new OrderBookLimitOrder(10, 99, OrderBookSide.Buy, 0));
        assertEquals(2, book.bids.size());
        var cancelledOrder = book.cancelOrder(res1.restingOrder().id());
        assertEquals(1, book.bids.size());
        assertNotNull(cancelledOrder);
    }

    @Test
    public void shouldNotBeAbleToCrossSelf() {
        var book = new OrderBook();
        var res1 = book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Buy, 1));
        var res2 = book.processOrder(new OrderBookLimitOrder(10, 99, OrderBookSide.Sell, 1));
        assertEquals(OrderBookLimitOrderResultStatus.Resting, res1.status());
        assertEquals(OrderBookLimitOrderResultStatus.Rejected, res2.status());
    }

    @Test
    public void cancelAll() {
        var book = new OrderBook();
        var res1 = book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Buy, 1));
        var res2 = book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Buy, 2));
        book.cancelAll(1);

        assertEquals(1, book.bids.size());
        assertEquals(2, book.bids.get(0).accountId());
    }

    @Test
    public void improvementTest() {
        var book = new OrderBook();
        book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Buy, 1));
        book.processOrder(new OrderBookLimitOrder(10, 99, OrderBookSide.Buy, 2));
        book.processOrder(new OrderBookLimitOrder(10, 101, OrderBookSide.Sell, 1));
        book.processOrder(new OrderBookLimitOrder(10, 102, OrderBookSide.Sell, 2));

        assertEquals(2, book.bids.size());
        assertEquals(100, book.bids.get(0).price().longValue());
        assertEquals(99, book.bids.get(1).price().longValue());
        assertEquals(2, book.asks.size());
        assertEquals(101, book.asks.get(0).price().longValue());
        assertEquals(102, book.asks.get(1).price().longValue());
    }
}
