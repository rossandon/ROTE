package shared.service.results;

import shared.orderBook.OrderBookEntry;

import java.util.ArrayList;

public record OrderBookSnapshot(String instrumentCode, long sequence, ArrayList<OrderBookEntry> bids, ArrayList<OrderBookEntry> asks) {
}
