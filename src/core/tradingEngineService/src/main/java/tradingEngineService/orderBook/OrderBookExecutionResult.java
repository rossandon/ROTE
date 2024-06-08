package tradingEngineService.orderBook;

import shared.orderBook.OrderBookTrade;

import java.math.BigDecimal;
import java.util.ArrayList;

public record OrderBookExecutionResult(OrderBookLimitOrder order, BigDecimal executed, ArrayList<OrderBookTrade> trades) {
    public boolean partial() {
        return executed.compareTo(order.size()) < 0;
    }

    public BigDecimal remaining() {
        return order.size().subtract(executed);
    }

    public OrderBookLimitOrder getRestingOrder() {
        return new OrderBookLimitOrder(remaining(), order.price(), order.side(), order.accountId());
    }
}
