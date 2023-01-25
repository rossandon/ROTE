package roteWeb.utils;

import org.springframework.stereotype.Component;
import roteShared.service.TradingEngineServiceRequest;
import roteShared.service.TradingEngineServiceResponse;
import roteShared.kafka.KafkaRequestResponseClientExecutor;

@Component
public class WebKafkaRequestResponseClientExecutor extends KafkaRequestResponseClientExecutor<String, TradingEngineServiceRequest, TradingEngineServiceResponse> {
}
