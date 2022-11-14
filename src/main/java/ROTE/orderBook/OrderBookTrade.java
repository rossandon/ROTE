package ROTE.orderBook;

public record OrderBookTrade(long size, long price, OrderBookSide takerSide, long makerAccountId, long takerAccountId) {
}
