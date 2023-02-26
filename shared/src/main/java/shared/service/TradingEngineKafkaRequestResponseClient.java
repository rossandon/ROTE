package shared.service;

import shared.kafka.KafkaRequestResponseClient;
import org.springframework.stereotype.Component;
import shared.kafka.RoteKafkaAdminClient;
import shared.kafka.RoteKafkaConsumer;
import shared.kafka.RoteKafkaProducer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class TradingEngineKafkaRequestResponseClient extends KafkaRequestResponseClient<String, TradingEngineServiceRequest, TradingEngineServiceResponse> {

    static List<TradingEngineServiceRequestType> readRequests = Arrays.stream(new TradingEngineServiceRequestType[] {
            TradingEngineServiceRequestType.GetBalances,
            TradingEngineServiceRequestType.GetBalance,
            TradingEngineServiceRequestType.GetBook,
            TradingEngineServiceRequestType.Error,
    }).toList();

    public TradingEngineKafkaRequestResponseClient(RoteKafkaProducer<String, TradingEngineServiceRequest> kafkaProducer, RoteKafkaConsumer kafkaConsumer, RoteKafkaAdminClient kafkaAdminClient) {
        super(kafkaProducer, kafkaConsumer, kafkaAdminClient);
    }

    public TradingEngineServiceResponse send(TradingEngineServiceRequest request) throws Exception {
        return send(getTopic(request), "", request);
    }

    public CompletableFuture<TradingEngineServiceResponse> sendAsync(TradingEngineServiceRequest request) throws Exception {
        return sendAsync(getTopic(request), "", request);
    }

    private static String getTopic(TradingEngineServiceRequest request) {
        return readRequests.contains(request.type())
                ? TradingEngineServiceConsts.ReadRequestTopic
                : TradingEngineServiceConsts.WriteRequestTopic;
    }
}
