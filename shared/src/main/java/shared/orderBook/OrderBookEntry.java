package shared.orderBook;

public record OrderBookEntry(long size, long price, long accountId, long id, OrderBookSide side) {
    public OrderBookEntry withSize(long size) {
        return new OrderBookEntry(size, price, accountId, id, side);
    }
}
