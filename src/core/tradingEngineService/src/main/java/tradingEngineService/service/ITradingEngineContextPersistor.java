package tradingEngineService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import tradingEngineService.tradingEngine.TradingEngineContext;

public interface ITradingEngineContextPersistor {
    void save(TradingEngineContext context) throws JsonProcessingException;
}
