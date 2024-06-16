package tradingEngineService.orderBook;

import shared.orderBook.OrderBookEntry;
import shared.orderBook.OrderBookSide;
import shared.orderBook.OrderBookTrade;

import java.math.BigDecimal;

public record OrderBookLimitOrder(BigDecimal size, BigDecimal price, OrderBookSide side, long accountId) {
    public OrderBookLimitOrder(long size, long price, OrderBookSide side, long accountId) {
        this(BigDecimal.valueOf(size), BigDecimal.valueOf(price), side, accountId);
    }

    public boolean canFill(OrderBookEntry entry) {
        return side == OrderBookSide.Buy ? price.compareTo(entry.price()) >= 0 : price.compareTo(entry.price()) <= 0;
    }
}
