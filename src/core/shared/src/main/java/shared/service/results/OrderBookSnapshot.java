package shared.service.results;

import shared.orderBook.OrderBookEntry;

import java.util.ArrayList;

public record OrderBookSnapshot(String instrumentCode, ArrayList<OrderBookEntry> bids, ArrayList<OrderBookEntry> asks) {
}
