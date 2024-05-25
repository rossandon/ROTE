package shared.service;

import shared.service.results.*;

public record TradingEngineServiceResponse(LimitOrderResult limitOrderResult, GetBalanceResult getBalanceResult,
                                           CancelOrderResult cancelOrderResult, GetBalancesResult getBalancesResult,
                                           OrderBookSnapshot orderBookSnapshot, TradingEngineErrorResult tradingEngineErrorResult) {

    public TradingEngineServiceResponse(LimitOrderResult result) {
        this(result, null, null, null, null, null);
    }

    public TradingEngineServiceResponse(GetBalanceResult result) {
        this(null, result, null, null, null, null);
    }

    public TradingEngineServiceResponse(GetBalancesResult result) {
        this(null, null, null, result, null, null);
    }

    public TradingEngineServiceResponse(CancelOrderResult result) {
        this(null, null, result, null, null, null);
    }

    public TradingEngineServiceResponse(OrderBookSnapshot result) {
        this(null, null, null, null, result, null);
    }

    public TradingEngineServiceResponse(TradingEngineErrorResult result) { this(null, null, null, null, null, result); }

    public TradingEngineServiceResponse() {
        this(null, null, null, null, null, null);
    }

    public void assertOk() throws Exception {
        if (tradingEngineErrorResult == null)
            return;
        throw new Exception(tradingEngineErrorResult.message());
    }
}
