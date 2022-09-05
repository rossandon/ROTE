package tradingEngine;

import orderBook.*;
import referential.Instrument;

public record InstrumentOrderBook(Instrument instrument, OrderBook orderBook) {}
