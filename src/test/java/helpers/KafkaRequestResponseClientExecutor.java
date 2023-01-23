package helpers;

import roteService.kafka.KafkaRequestResponseClient;
import roteService.service.TradingEngineServiceRequest;
import roteService.service.TradingEngineServiceResponse;
import roteService.utils.RunnableExecutor;

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
