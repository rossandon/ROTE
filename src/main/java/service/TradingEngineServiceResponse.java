package service;

import tradingEngine.LimitOrderResult;

public record TradingEngineServiceResponse(LimitOrderResult limitOrderResult, GetBalanceResult result) {
}
