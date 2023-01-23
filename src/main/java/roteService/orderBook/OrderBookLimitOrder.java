package roteService.orderBook;

public record OrderBookLimitOrder(long size, long price, OrderBookSide side, long accountId) {
    public OrderBookTrade fill(OrderBookEntry entry) {
        return new OrderBookTrade(Math.min(size, entry.size()), entry.price(), side, entry.accountId(), accountId);
    }

    public boolean canFill(OrderBookEntry entry) {
        return side == OrderBookSide.Buy ? price >= entry.price() : price <= entry.price();
    }
}
