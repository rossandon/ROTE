package tradingEngineService.service;

import tradingEngineService.orderBook.OrderBook;

public record MarketDataUpdate(String instrumentCode, OrderBook orderBook) {
}
