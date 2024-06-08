package webService.api.models;

import java.math.BigDecimal;

public record OrderBookEntryModel(BigDecimal size, BigDecimal price, boolean canCancel, long id) {
    public OrderBookEntryModel(long currentAccountId, shared.orderBook.OrderBookEntry entry) {
        this(entry.size(), entry.price(), entry.accountId() == currentAccountId, entry.id());
    }
}
