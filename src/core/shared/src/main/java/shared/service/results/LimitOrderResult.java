package shared.service.results;

import shared.orderBook.LimitOrderResultStatus;
import shared.orderBook.OrderBookLimitOrderResult;

public record LimitOrderResult(LimitOrderResultStatus type, OrderBookLimitOrderResult result) {}
