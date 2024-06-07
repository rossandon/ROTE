package webService.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.utils.RunnableExecutor;

@Component
public class MarketDataConsumerExecutor extends RunnableExecutor {
    @Autowired
    private MarketDataConsumer marketDataConsumer;

    @Override
    protected Runnable getBean() {
        return marketDataConsumer;
    }
}
