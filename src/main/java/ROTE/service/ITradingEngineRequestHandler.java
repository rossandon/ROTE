package ROTE.service;

@FunctionalInterface
public interface ITradingEngineRequestHandler {
    TradingEngineServiceResponse handle(TradingEngineServiceRequest request);
}
