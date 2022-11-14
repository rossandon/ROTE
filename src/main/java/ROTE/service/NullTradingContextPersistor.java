package ROTE.service;

import ROTE.tradingEngine.TradingEngineContext;
import org.springframework.stereotype.Component;

@Component
public class NullTradingContextPersistor implements ITradingContextPersistor {
    @Override
    public void save(TradingEngineContext context) {
    }
}
