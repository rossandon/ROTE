package webService.api.models;

import shared.orderBook.OrderBookEntry;
import shared.service.results.OrderBookSnapshot;

import java.util.List;

public record GetBookResponse(String instrumentCode, List<OrderBookEntryModel> bids, List<OrderBookEntryModel> asks) {
    public GetBookResponse(long currentAccountId, OrderBookSnapshot orderBookSnapshot) {
        this(orderBookSnapshot.instrumentCode(),
                orderBookSnapshot.bids().stream().map(e -> new OrderBookEntryModel(currentAccountId, e)).toList(),
                orderBookSnapshot.asks().stream().map(e -> new OrderBookEntryModel(currentAccountId, e)).toList());
    }
}
