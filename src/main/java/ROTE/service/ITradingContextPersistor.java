package ROTE.service;

import ROTE.tradingEngine.TradingEngineContext;

public interface ITradingContextPersistor {
    void save(TradingEngineContext context);
}
