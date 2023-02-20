package shared.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class RoteKafkaAdminClient {
    private final AdminClient client;
    private final String namespace;

    public RoteKafkaAdminClient(KafkaConfigurationProvider kafkaConfigurationProvider) {
        this.client = AdminClient.create(kafkaConfigurationProvider.buildProps());
        this.namespace = kafkaConfigurationProvider.getEnvironmentName();
    }

    public void createTopic(String name, int partitionCount) {
        name = KafkaHelpers.getNamespacedTopic(name, namespace);
        client.createTopics(Collections.singleton(new NewTopic(name, Optional.of(partitionCount), Optional.empty())));
    }
}
