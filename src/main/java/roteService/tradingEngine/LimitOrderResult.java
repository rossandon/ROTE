package roteService.tradingEngine;

import roteService.orderBook.OrderBookLimitOrderResult;

public record LimitOrderResult(LimitOrderResultStatus type, OrderBookLimitOrderResult result) {}
