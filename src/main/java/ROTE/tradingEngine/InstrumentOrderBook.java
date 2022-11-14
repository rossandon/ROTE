package ROTE.tradingEngine;

import ROTE.orderBook.OrderBook;
import ROTE.referential.Instrument;

public record InstrumentOrderBook(Instrument instrument, OrderBook orderBook) {}
