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
    private final ProcessingQueue controlMessageQueue = new ProcessingQueue();

    private boolean closed;

    public RoteKafkaConsumer(KafkaConfigurationProvider kafkaConfigurationProvider) {
        this.namespace = kafkaConfigurationProvider.getEnvironmentName();
        this.props = kafkaConfigurationProvider.buildConsumerProps();
    }

    public <TKey, TValue> void consumePartitions(Collection<TopicPartitionAndOffet> topics, boolean autoCommit,
                                                 IKafkaConsumerHandler<TKey, TValue> handler,
                                                 IKafkaControlMessageHandler controlMessageHandler) {
        var consumerProps = (Properties) props.clone();
        consumerProps.put("enable.auto.commit", Boolean.toString(autoCommit).toLowerCase());
        try (var consumer = new KafkaConsumer<TKey, TValue>(consumerProps)) {
            var topicPartitions = topics.stream().map(t -> new TopicPartition(KafkaHelpers.getNamespacedTopic(t.topic, namespace), t.partition)).toList();
            consumer.assign(topicPartitions);

            for (var topic : topics) {
                if (topic.offset.isPresent())
                    consumer.seek(new TopicPartition(KafkaHelpers.getNamespacedTopic(topic.topic, namespace), topic.partition), topic.offset.get());
            }

            while (!closed) {
                var results = consumer.poll(Duration.ofSeconds(1));
                for (var result : results) {
                    handler.handle(result);
                }

                while (true) {
                    var item = controlMessageQueue.dequeue();
                    if (item == null) break;
                    log.debug("Processing control message");
                    controlMessageHandler.handle(item);
                    log.debug("Processed control message");
                }
            }
        }
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
            consumer.subscribe(List.of(namespacedTopic), new SeekFromEndListener<>(consumer, lookback));
            while (!closed) {
                var results = consumer.poll(Duration.ofSeconds(1));
                for (var result : results) {
                    handler.handle(result);
                }
            }
        }
    }

    public Future<Object> queueControlMessage(Object obj) {
        return controlMessageQueue.queue(obj);
    }

    @Override
    public void close() {
        closed = true;
    }

    public record TopicPartitionAndOffet(String topic, int partition, Optional<Long> offset) {
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
