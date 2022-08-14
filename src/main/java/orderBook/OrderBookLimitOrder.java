package orderBook;

public record OrderBookLimitOrder(long size, long price, OrderBookSide side, long accountId) {
    public OrderBookTrade tryFill(OrderBookEntry entry) {
        var canFill = side == OrderBookSide.Buy ? price >= entry.price() : price <= entry.price();
        if (!canFill)
            return null;
        return new OrderBookTrade(Math.min(size, entry.size()), entry.price(), side);
    }
}
