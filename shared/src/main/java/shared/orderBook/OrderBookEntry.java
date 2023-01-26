package shared.orderBook;

public record OrderBookEntry(long size, long price, long accountId, long id) {
    public OrderBookEntry withSize(long size) {
        return new OrderBookEntry(size, price, accountId, id);
    }
    public long fundingSize() {
        return price * size;
    }
}
