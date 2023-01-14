package ROTE.service;

import ROTE.tradingEngine.TradingEngineContext;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ITradingEngineContextPersistor {
    void save(TradingEngineContext context) throws JsonProcessingException;
}
