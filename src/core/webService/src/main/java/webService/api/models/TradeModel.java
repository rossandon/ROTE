package webService.api.models;

import shared.orderBook.OrderBookSide;
import shared.service.results.Trade;

import java.math.BigDecimal;

public record TradeModel(String instrumentCode, String timestamp, long id, boolean isYours, BigDecimal size, BigDecimal price, OrderBookSide takerSide) {
    public TradeModel(long currentAccountId, Trade trade) {
        this(trade.instrumentCode(),
                trade.timestamp(),
                trade.id(),
                trade.makerAccountId() == currentAccountId || trade.takerAccountId() == currentAccountId,
                trade.size(),
                trade.price(),
                trade.takerSide());
    }
}
