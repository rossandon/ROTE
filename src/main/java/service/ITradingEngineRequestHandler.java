package service;

@FunctionalInterface
public interface ITradingEngineRequestHandler {
    TradingEngineServiceResponse handle(TradingEngineServiceRequest request);
}
