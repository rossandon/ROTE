package shared.orderBook;

import java.util.ArrayList;

public record OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus status, ArrayList<OrderBookTrade> trades, OrderBookEntry restingOrder) {
}
