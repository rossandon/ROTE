package roteShared.service;

import org.springframework.stereotype.Component;
import roteShared.kafka.KafkaConfigurationProvider;
import roteShared.kafka.KafkaRequestResponseClient;

@Component
public class TradingEngineKafkaRequestResponseClient extends KafkaRequestResponseClient<String, TradingEngineServiceRequest, TradingEngineServiceResponse> {
    public TradingEngineKafkaRequestResponseClient(KafkaConfigurationProvider kafkaConfigurationProvider) {
        super(kafkaConfigurationProvider);
    }

    public TradingEngineServiceResponse send(TradingEngineServiceRequest request) throws Exception {
        return send(TradingEngineServiceConsts.RequestTopic, "", request);
    }
}
