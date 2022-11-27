package helpers;

import ROTE.kafka.KafkaRequestResponseClient;
import ROTE.service.TradingEngineServiceRequest;
import ROTE.service.TradingEngineServiceResponse;
import ROTE.utils.RunnableExecutor;

public class KafkaRequestResponseClientExecutor extends RunnableExecutor {
    private final KafkaRequestResponseClient<String, TradingEngineServiceRequest, TradingEngineServiceResponse> client;

    public KafkaRequestResponseClientExecutor(KafkaRequestResponseClient<String, TradingEngineServiceRequest, TradingEngineServiceResponse> client) {
        this.client = client;
    }

    @Override
    protected Runnable getBean() {
        return client;
    }
}
