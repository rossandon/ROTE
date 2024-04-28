package webService.api.models;

import shared.orderBook.OrderBookEntry;
import shared.service.results.OrderBookSnapshot;

import java.util.List;

public record GetBookResponse(String instrumentCode, List<OrderBookEntry> bids, List<OrderBookEntry> asks) {
    public GetBookResponse(OrderBookSnapshot orderBookSnapshot) {
        this(orderBookSnapshot.instrumentCode(), orderBookSnapshot.bids(), orderBookSnapshot.asks());
    }
}
