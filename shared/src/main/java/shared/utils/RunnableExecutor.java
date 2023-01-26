package shared.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.Executor;

public abstract class RunnableExecutor implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private Executor executor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        executor.execute(getBean());
    }

    protected abstract Runnable getBean();
}
