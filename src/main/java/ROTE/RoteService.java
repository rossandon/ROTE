package ROTE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
@ComponentScan
@ConfigurationPropertiesScan
public class RoteService {
    public static ApplicationContext create() {
        var app = new SpringApplication(RoteService.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        return app.run();
    }

    @Bean
    public TaskExecutor getExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
