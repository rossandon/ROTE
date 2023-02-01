package shared.service;

import shared.kafka.KafkaRequestResponseClient;
import org.springframework.stereotype.Component;
import shared.kafka.RoteKafkaConsumer;
import shared.kafka.RoteKafkaProducer;

@Component
public class TradingEngineKafkaRequestResponseClient extends KafkaRequestResponseClient<String, TradingEngineServiceRequest, TradingEngineServiceResponse> {
    public TradingEngineKafkaRequestResponseClient(RoteKafkaProducer<String, TradingEngineServiceRequest> kafkaProducer, RoteKafkaConsumer kafkaConsumer) {
        super(kafkaProducer, kafkaConsumer);
    }

    public TradingEngineServiceResponse send(TradingEngineServiceRequest request) throws Exception {
        return send(TradingEngineServiceConsts.RequestTopic, "", request);
    }
}
