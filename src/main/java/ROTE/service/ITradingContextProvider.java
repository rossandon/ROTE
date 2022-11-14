package ROTE.service;

import ROTE.tradingEngine.TradingEngineContext;

public interface ITradingContextProvider {
    TradingEngineContext getLatest();
}
