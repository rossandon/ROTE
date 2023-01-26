package shared.service;

import shared.orderBook.LimitOrderResultStatus;
import shared.orderBook.OrderBookLimitOrderResult;

public record LimitOrderResult(LimitOrderResultStatus type, OrderBookLimitOrderResult result) {}
