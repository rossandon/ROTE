package roteService.service;

import roteShared.service.TradingEngineServiceRequest;
import roteShared.service.TradingEngineServiceResponse;

@FunctionalInterface
public interface ITradingEngineRequestHandler {
    TradingEngineServiceResponse handle(TradingEngineServiceRequest request);
}
