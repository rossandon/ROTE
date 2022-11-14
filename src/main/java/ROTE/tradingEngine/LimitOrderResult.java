package ROTE.tradingEngine;

import ROTE.orderBook.OrderBookLimitOrderResult;

public record LimitOrderResult(LimitOrderResultStatus type, OrderBookLimitOrderResult result) {}
