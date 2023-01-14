package ROTE.service;

import ROTE.tradingEngine.ITradingEngineContextProvider;
import ROTE.tradingEngine.TradingEngineContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "tradingEngineContext", name = "provider", havingValue = "fresh")
public class EphemeralTradingEngineContextProvider implements ITradingEngineContextProvider {
    @Bean
    public TradingEngineContext getContext() {
        return new TradingEngineContext();
    }
}
