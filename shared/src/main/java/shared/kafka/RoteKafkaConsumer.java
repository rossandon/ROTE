package shared.kafka;

import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;
import shared.utils.ProcessingQueue;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Future;

@Component
public class RoteKafkaConsumer implements AutoCloseable {
    private static final Logger log = Logger.getLogger(RoteKafkaConsumer.class);

    private final String namespace;
    private final Properties props;
    private final ProcessingQueue controlMessageQueue = new ProcessingQueue();

    private boolean closed;

    public RoteKafkaConsumer(KafkaConfigurationProvider kafkaConfigurationProvider) {
        this.namespace = kafkaConfigurationProvider.getEnvironmentName();
        this.props = kafkaConfigurationProvider.buildProps();
    }

    public <TKey, TValue> void consume(String topic, long offset, boolean autoCommit,
                                       IKafkaConsumerHandler<TKey, TValue> handler,
                                       IKafkaControlMessageHandler controlMessageHandler) {
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

                while (true) {
                    var item = controlMessageQueue.dequeue();
                    if (item == null) break;
                    controlMessageHandler.handle(item);
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
