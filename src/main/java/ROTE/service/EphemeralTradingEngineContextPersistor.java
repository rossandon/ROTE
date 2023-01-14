package ROTE.service;

import ROTE.tradingEngine.TradingEngineContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "tradingEngineContext", name = "provider", havingValue = "fresh")
public class EphemeralTradingEngineContextPersistor implements ITradingEngineContextPersistor {
    @Override
    public void save(TradingEngineContext context) {
    }
}
