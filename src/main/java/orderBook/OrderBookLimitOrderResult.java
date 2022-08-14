package orderBook;

import java.util.ArrayList;

public record OrderBookLimitOrderResult(ArrayList<OrderBookTrade> trades, OrderBookEntry restingOrder) {
}
