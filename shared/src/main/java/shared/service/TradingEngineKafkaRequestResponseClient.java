package shared.service;

import shared.kafka.KafkaConfigurationProvider;
import shared.kafka.KafkaRequestResponseClient;
import org.springframework.stereotype.Component;

@Component
public class TradingEngineKafkaRequestResponseClient extends KafkaRequestResponseClient<String, TradingEngineServiceRequest, TradingEngineServiceResponse> {
    public TradingEngineKafkaRequestResponseClient(KafkaConfigurationProvider kafkaConfigurationProvider) {
        super(kafkaConfigurationProvider);
    }

    public TradingEngineServiceResponse send(TradingEngineServiceRequest request) throws Exception {
        return send(TradingEngineServiceConsts.RequestTopic, "", request);
    }
}
