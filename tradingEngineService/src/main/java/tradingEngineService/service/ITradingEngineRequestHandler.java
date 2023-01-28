package tradingEngineService.service;

import shared.service.TradingEngineServiceRequest;
import shared.service.TradingEngineServiceResponse;

@FunctionalInterface
public interface ITradingEngineRequestHandler {
    TradingEngineServiceResponse handle(TradingEngineServiceRequest request) throws Exception;
}
