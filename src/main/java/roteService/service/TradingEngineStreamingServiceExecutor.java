package roteService.service;

import roteUtils.RunnableExecutor;
import org.springframework.stereotype.Component;

@Component
public class TradingEngineStreamingServiceExecutor extends RunnableExecutor {
    private final TradingEngineStreamingService tradingEngineStreamingService;

    public TradingEngineStreamingServiceExecutor(TradingEngineStreamingService tradingEngineStreamingService) {
        this.tradingEngineStreamingService = tradingEngineStreamingService;
    }

    @Override
    protected Runnable getBean() {
        return tradingEngineStreamingService;
    }
}
