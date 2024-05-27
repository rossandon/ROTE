package webService;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
@ComponentScan({"webService", "shared"})
@ConfigurationPropertiesScan({"webService", "shared"})
public class RoteWebService {
    @Bean
    public TaskExecutor getExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
