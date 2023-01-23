package roteService.tradingEngine;

import roteService.orderBook.OrderBook;
import roteService.referential.Instrument;

public record InstrumentOrderBook(Instrument instrument, OrderBook orderBook) {}
