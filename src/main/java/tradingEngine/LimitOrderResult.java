package tradingEngine;

import orderBook.OrderBookLimitOrderResult;

public record LimitOrderResult(LimitOrderResultStatus type, OrderBookLimitOrderResult result) {}
