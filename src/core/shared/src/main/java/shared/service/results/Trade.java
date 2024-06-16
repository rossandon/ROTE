package shared.service.results;

import shared.orderBook.OrderBookSide;

import java.math.BigDecimal;

public record Trade(String instrumentCode, long id, BigDecimal size, BigDecimal price, OrderBookSide takerSide) {
}
