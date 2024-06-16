package webService.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.utils.RunnableExecutor;

@Component
public class TradeTapeConsumerExecutor extends RunnableExecutor {
    @Autowired
    private TradeTapeConsumer tradeTapeConsumer;

    @Override
    protected Runnable getBean() {
        return tradeTapeConsumer;
    }
}
