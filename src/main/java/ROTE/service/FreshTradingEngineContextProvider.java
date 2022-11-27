package ROTE.service;

import ROTE.tradingEngine.TradingEngineContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FreshTradingEngineContextProvider {
    @Bean
    @ConditionalOnProperty(prefix = "tradingEngineContext", name = "provider", havingValue = "fresh")
    public TradingEngineContext getFreshContext() {
        return new TradingEngineContext();
    }
}
