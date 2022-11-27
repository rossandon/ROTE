package ROTE.service;

import ROTE.tradingEngine.TradingEngineContext;
import org.springframework.stereotype.Component;

@Component
public class NullTradingEngineContextPersistor implements ITradingEngineContextPersistor {
    @Override
    public void save(TradingEngineContext context) {
    }
}
