package roteService.service;

import roteService.tradingEngine.LimitOrderResult;

public record TradingEngineServiceResponse(LimitOrderResult limitOrderResult, GetBalanceResult getBalanceResult,
                                           CancelOrderResult cancelOrderResult, GetBalancesResult getBalancesResult) {

    public TradingEngineServiceResponse(LimitOrderResult result) {
        this(result, null, null, null);
    }

    public TradingEngineServiceResponse(GetBalanceResult result) {
        this(null, result, null, null);
    }

    public TradingEngineServiceResponse(GetBalancesResult result) {
        this(null, null, null, result);
    }

    public TradingEngineServiceResponse(CancelOrderResult result) {
        this(null, null, result, null);
    }

    public TradingEngineServiceResponse() {
        this(null, null, null, null);
    }
}
