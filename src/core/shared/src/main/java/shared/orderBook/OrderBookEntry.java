package shared.orderBook;

import java.math.BigDecimal;

public record OrderBookEntry(BigDecimal size, BigDecimal price, long accountId, long id, OrderBookSide side) {
    public OrderBookEntry withSize(BigDecimal size) {
        return new OrderBookEntry(size, price, accountId, id, side);
    }
}
