package webService.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.utils.RunnableExecutor;
import shared.service.TradingEngineKafkaRequestResponseClient;

@Component
public class WebKafkaRequestResponseClientExecutor extends RunnableExecutor {
    @Autowired
    private TradingEngineKafkaRequestResponseClient client;

    @Override
    protected Runnable getBean() {
        return client;
    }
}
