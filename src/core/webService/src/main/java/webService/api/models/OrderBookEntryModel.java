package webService.api.models;

public record OrderBookEntryModel(long size, long price, boolean canCancel, long id) {
    public OrderBookEntryModel(long currentAccountId, shared.orderBook.OrderBookEntry entry) {
        this(entry.size(), entry.price(), entry.accountId() == currentAccountId, entry.id());
    }
}
