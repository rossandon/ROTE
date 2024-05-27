package shared.orderBook;

import java.util.ArrayList;

public record OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus status, String rejectReason, ArrayList<OrderBookTrade> trades, OrderBookEntry restingOrder) {
}
