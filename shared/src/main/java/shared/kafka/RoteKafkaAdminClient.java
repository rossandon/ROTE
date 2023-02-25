package shared.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class RoteKafkaAdminClient {
    private static final Logger log = Logger.getLogger(KafkaRequestResponseClient.class);

    private final AdminClient client;
    private final String namespace;

    public RoteKafkaAdminClient(KafkaConfigurationProvider kafkaConfigurationProvider) {
        this.client = AdminClient.create(kafkaConfigurationProvider.buildProps());
        this.namespace = kafkaConfigurationProvider.getEnvironmentName();
    }

    public void createTopic(String name, int partitionCount) throws ExecutionException, InterruptedException, TimeoutException {
        name = KafkaHelpers.getNamespacedTopic(name, namespace);
        var result = client.createTopics(Collections.singleton(new NewTopic(name, Optional.of(partitionCount), Optional.empty())));
        result.all().get(30, TimeUnit.SECONDS);
        log.info("Created topic " + name);
    }
}
