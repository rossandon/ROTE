package shared.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RoteKafkaAdminClient implements AutoCloseable {
    private static final Logger log = Logger.getLogger(KafkaRequestResponseClient.class);

    private final AdminClient client;
    private final String namespace;

    public RoteKafkaAdminClient(KafkaConfigurationProvider kafkaConfigurationProvider) {
        this.client = AdminClient.create(kafkaConfigurationProvider.buildAdminProps());
        this.namespace = kafkaConfigurationProvider.getEnvironmentName();
    }

    public void createTopic(String name, int partitionCount) throws ExecutionException, InterruptedException, TimeoutException {
        name = KafkaHelpers.getNamespacedTopic(name, namespace);
        try {
            var result = client.createTopics(Collections.singleton(new NewTopic(name, Optional.of(partitionCount), Optional.empty())));
            result.values().get(name).get();
            log.info("Created topic " + name);
        }
        catch (InterruptedException | ExecutionException e) {
            if (!(e.getCause() instanceof TopicExistsException))
                throw new RuntimeException(e.getMessage(), e);
            else {
                log.info("Topic " + name + " already exists");
            }
        }
    }

    public void close() {
        this.client.close();
    }
}
