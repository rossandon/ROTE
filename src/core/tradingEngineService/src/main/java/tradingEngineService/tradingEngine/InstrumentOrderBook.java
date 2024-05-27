package tradingEngineService.tradingEngine;

import tradingEngineService.orderBook.OrderBook;
import tradingEngineService.referential.Instrument;

public record InstrumentOrderBook(Instrument instrument, OrderBook orderBook) {}
