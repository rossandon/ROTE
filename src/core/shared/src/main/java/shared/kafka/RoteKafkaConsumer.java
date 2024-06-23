package shared.kafka;

import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import shared.utils.ProcessingQueue;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RoteKafkaConsumer implements AutoCloseable {
    private static final Logger log = Logger.getLogger(RoteKafkaConsumer.class);

    private final String namespace;
    private final Properties props;
    private boolean closed;

    public RoteKafkaConsumer(KafkaConfigurationProvider kafkaConfigurationProvider) {
        this.namespace = kafkaConfigurationProvider.getEnvironmentName();
        this.props = kafkaConfigurationProvider.buildConsumerProps();
    }

    public <TKey, TValue> void consumePartition(String topic, int partition, long offset, boolean autoCommit,
                                                IKafkaConsumerHandler<TKey, TValue> handler) {
        var consumerProps = (Properties) props.clone();
        consumerProps.put("enable.auto.commit", Boolean.toString(autoCommit).toLowerCase());
        try (var consumer = new KafkaConsumer<TKey, TValue>(consumerProps)) {
            var namespacedTopic = KafkaHelpers.getNamespacedTopic(topic, namespace);
            var topicPartition = new TopicPartition(namespacedTopic, 0);
            consumer.assign(Collections.singleton(topicPartition));
            consumer.seek(topicPartition, offset);
            while (!closed) {
                var results = consumer.poll(Duration.ofSeconds(1));
                for (var result : results) {
                    handler.handle(result);
                }
            }
        }
    }

    public <TKey, TValue> void consumeFromEnd(String topic, int lookback, IKafkaConsumerHandler<TKey, TValue> handler) {
        var consumerProps = (Properties) props.clone();
        consumerProps.put("enable.auto.commit", false);
        try (var consumer = new KafkaConsumer<TKey, TValue>(consumerProps)) {
            var namespacedTopic = KafkaHelpers.getNamespacedTopic(topic, namespace);
            var partitions = consumer.partitionsFor(namespacedTopic)
                    .stream()
                    .map(p -> new TopicPartition(p.topic(), p.partition()))
                    .toList();
            consumer.assign(partitions);
            var topicPartitionLongMap = consumer.endOffsets(partitions);
            topicPartitionLongMap.forEach((partition, offset) ->
            {
                var seekTo = Math.max(offset - lookback, 0);
                log.info(String.format("Seek %s to %d", partition.topic(), seekTo));
                consumer.seek(partition, seekTo);
            });
            while (!closed) {
                var results = consumer.poll(Duration.ofSeconds(1));
                for (var result : results) {
                    handler.handle(result);
                }
            }
        }
    }

    @Override
    public void close() {
        closed = true;
    }

    public record KafkaHeader(String key, byte[] value) implements Header {
        @Override
        public String key() {
            return key;
        }

        @Override
        public byte[] value() {
            return value;
        }
    }
}
