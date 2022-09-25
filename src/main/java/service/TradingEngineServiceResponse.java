package service;

import tradingEngine.LimitOrderResult;

public record TradingEngineServiceResponse(LimitOrderResult limitOrderResult, GetBalanceResult getBalanceResult,
                                           CancelOrderResult cancelOrderResult) {
    public TradingEngineServiceResponse(LimitOrderResult result) {
        this(result, null, null);
    }

    public TradingEngineServiceResponse(GetBalanceResult result) {
        this(null, result, null);
    }

    public TradingEngineServiceResponse(CancelOrderResult result) {
        this(null, null, result);
    }

    public TradingEngineServiceResponse() {
        this(null, null, null);
    }
}
