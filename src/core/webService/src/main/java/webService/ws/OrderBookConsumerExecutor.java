package webService.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.utils.RunnableExecutor;

@Component
public class OrderBookConsumerExecutor extends RunnableExecutor {
    @Autowired
    private OrderBookConsumer orderBookConsumer;

    @Override
    protected Runnable getBean() {
        return orderBookConsumer;
    }
}
