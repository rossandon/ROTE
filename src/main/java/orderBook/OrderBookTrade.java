package orderBook;

public record OrderBookTrade(long size, long price, OrderBookSide side) {
}
