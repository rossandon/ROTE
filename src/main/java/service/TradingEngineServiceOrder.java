package service;

public record TradingEngineServiceOrder(long size, long price, long accountId, String instrumentCode) {
}
