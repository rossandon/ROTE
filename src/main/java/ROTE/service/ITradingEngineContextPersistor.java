package ROTE.service;

import ROTE.tradingEngine.TradingEngineContext;

public interface ITradingEngineContextPersistor {
    void save(TradingEngineContext context);
}
