package shared.orderBook;

import java.math.BigDecimal;

public record OrderBookTrade(long id, BigDecimal size, BigDecimal price, OrderBookSide takerSide, long makerAccountId, long takerAccountId) {
}
