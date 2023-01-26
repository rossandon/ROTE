package tradingEngineService.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import tradingEngineService.tradingEngine.ITradingEngineContextProvider;
import tradingEngineService.tradingEngine.TradingEngineContext;

@Component
@ConditionalOnProperty(prefix = "tradingEngineContext", name = "provider", havingValue = "fresh")
public class EphemeralTradingEngineContextProvider implements ITradingEngineContextProvider {
    @Bean
    public TradingEngineContext getContext() {
        return new TradingEngineContext();
    }
}
