package shared.orderBook;

import java.math.BigDecimal;

public record OrderBookTrade(BigDecimal size, BigDecimal price, OrderBookSide takerSide, long makerAccountId, long takerAccountId) {
}
