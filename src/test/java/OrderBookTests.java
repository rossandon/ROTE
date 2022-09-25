import orderBook.OrderBook;
import orderBook.OrderBookLimitOrder;
import orderBook.OrderBookLimitOrderResultStatus;
import orderBook.OrderBookSide;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderBookTests {
    @Test
    public void limitOrderShouldRest() {
        var book = new OrderBook();
        book.processOrder(new OrderBookLimitOrder(100, 100, OrderBookSide.Buy, 1));
        book.processOrder(new OrderBookLimitOrder(100, 99, OrderBookSide.Buy, 1));

        assertEquals(2, book.Bids.size());
        assertEquals(100, book.Bids.get(0).price());
        assertEquals(99, book.Bids.get(1).price());

        var res1 = book.processOrder(new OrderBookLimitOrder(49, 100, OrderBookSide.Sell, 2));
        var res2 = book.processOrder(new OrderBookLimitOrder(10, 101, OrderBookSide.Sell, 2));

        assertEquals(1, res1.trades().size());
        assertEquals(2, res1.trades().get(0).takerAccountId());
        assertEquals(1, res1.trades().get(0).makerAccountId());
        assertEquals(OrderBookSide.Sell, res1.trades().get(0).takerSide());

        assertNull(res1.restingOrder());
        assertNotNull(res2.restingOrder());
        assertNull(res2.trades());

        assertEquals(2, book.Bids.size());
        assertEquals(100, book.Bids.get(0).price());
        assertEquals(51, book.Bids.get(0).size());

        assertEquals(1, book.Asks.size());
    }

    @Test
    public void filledOrdersShouldBeRemoved() {
        var book = new OrderBook();
        book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Sell, 0));
        book.processOrder(new OrderBookLimitOrder(11, 101, OrderBookSide.Sell, 0));
        book.processOrder(new OrderBookLimitOrder(12, 102, OrderBookSide.Sell, 0));
        assertEquals(3, book.Asks.size());
        var res1 = book.processOrder(new OrderBookLimitOrder(40, 103, OrderBookSide.Buy, 1));
        assertEquals(3, res1.trades().size());
        assertEquals(100, res1.trades().get(0).price());
        assertEquals(101, res1.trades().get(1).price());
        assertEquals(102, res1.trades().get(2).price());
        assertEquals(0, book.Asks.size());
    }

    @Test
    public void cancelledAskShouldBeRemoved() {
        var book = new OrderBook();
        var res1 = book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Sell, 0));
        book.processOrder(new OrderBookLimitOrder(10, 101, OrderBookSide.Sell, 0));
        assertEquals(2, book.Asks.size());
        var cancelledOrder = book.cancelOrder(res1.restingOrder().id());
        assertEquals(1, book.Asks.size());
        assertNotNull(cancelledOrder);
    }

    @Test
    public void cancelledBidShouldBeRemoved() {
        var book = new OrderBook();
        var res1 = book.processOrder(new OrderBookLimitOrder(10, 100, OrderBookSide.Buy, 0));
        book.processOrder(new OrderBookLimitOrder(10, 99, OrderBookSide.Buy, 0));
        assertEquals(2, book.Bids.size());
        var cancelledOrder = book.cancelOrder(res1.restingOrder().id());
        assertEquals(1, book.Bids.size());
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
}
