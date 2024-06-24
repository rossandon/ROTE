package webService.api.models;

import shared.service.results.OrderBookSnapshot;

import java.util.List;

public record OrderBookSnapshotModel(String instrumentCode, List<OrderBookEntryModel> bids, List<OrderBookEntryModel> asks) {
    public OrderBookSnapshotModel(long currentAccountId, OrderBookSnapshot orderBookSnapshot) {
        this(orderBookSnapshot.instrumentCode(),
                orderBookSnapshot.bids().stream().map(e -> new OrderBookEntryModel(currentAccountId, e)).toList(),
                orderBookSnapshot.asks().stream().map(e -> new OrderBookEntryModel(currentAccountId, e)).toList());
    }
}
