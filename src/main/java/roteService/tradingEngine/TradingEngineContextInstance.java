package roteService.tradingEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TradingEngineContextInstance {
    private TradingEngineContext context;

    private final ITradingEngineContextProvider tradingEngineContextProvider;

    public TradingEngineContextInstance(TradingEngineContext tradingEngineContext) {
        context = tradingEngineContext;
        tradingEngineContextProvider = null;
    }

    @Autowired
    public TradingEngineContextInstance(ITradingEngineContextProvider tradingEngineContextProvider) {

        this.tradingEngineContextProvider = tradingEngineContextProvider;
    }

    public TradingEngineContext getContext() {
        if (context == null) {
            context = tradingEngineContextProvider.getContext();
        }

        return context;
    }

    public void reload() {
        context = tradingEngineContextProvider.getContext();
    }
}
