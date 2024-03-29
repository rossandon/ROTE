package shared.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import shared.utils.RunnableExecutor;

public class KafkaRequestResponseClientExecutor<TKey, TRequest, TResponse> extends RunnableExecutor {
    @Autowired
    private KafkaRequestResponseClient<TKey, TRequest, TResponse> client;

    @Override
    protected Runnable getBean() {
        return client;
    }
}
