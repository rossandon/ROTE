package roteService.service;

import roteService.tradingEngine.TradingEngineContext;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ITradingEngineContextPersistor {
    void save(TradingEngineContext context) throws JsonProcessingException;
}
