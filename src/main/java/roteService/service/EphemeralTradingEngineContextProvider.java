package roteService.service;

import roteService.tradingEngine.ITradingEngineContextProvider;
import roteService.tradingEngine.TradingEngineContext;
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
