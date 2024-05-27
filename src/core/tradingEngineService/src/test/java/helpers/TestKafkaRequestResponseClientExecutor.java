package helpers;

import org.springframework.beans.factory.annotation.Autowired;
import shared.utils.RunnableExecutor;
import shared.service.TradingEngineKafkaRequestResponseClient;

public class TestKafkaRequestResponseClientExecutor extends RunnableExecutor {
    @Autowired
    private TradingEngineKafkaRequestResponseClient client;

    @Override
    protected Runnable getBean() {
        return client;
    }
}
