package orderBook;

import java.util.ArrayList;

public record OrderBookExecutionResult(OrderBookLimitOrder order, long executed, ArrayList<OrderBookTrade> trades) {
    public boolean partial() {
        return executed < order.size();
    }

    public long remaining() {
        return order.size() - executed;
    }

    public OrderBookLimitOrder getRestingOrder() {
        return new OrderBookLimitOrder(remaining(), order.price(), order.side(), order.accountId());
    }
}
