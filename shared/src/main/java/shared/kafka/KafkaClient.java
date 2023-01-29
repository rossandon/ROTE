package shared.kafka;

import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;
import shared.utils.ProcessingQueue;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

@Component
public class KafkaClient<TProducerKey, TProducerValue> implements AutoCloseable {
    private static final Logger log = Logger.getLogger(KafkaClient.class);

    private final String namespace;
    private final Properties props;
    private final KafkaProducer<TProducerKey, TProducerValue> producer;
    private final ProcessingQueue controlMessageQueue = new ProcessingQueue();

    private boolean closed;

    public KafkaClient(KafkaConfigurationProvider kafkaConfigurationProvider) {
        this.namespace = kafkaConfigurationProvider.getEnvironmentName();
        this.props = kafkaConfigurationProvider.buildProps();
        this.producer = new KafkaProducer<>(props);
    }

    public <TKey, TValue> void consume(String topic, long offset, IKafkaConsumerHandler<TKey, TValue> handler,
                                       IKafkaControlMessageHandler controlMessageHandler) {
        var consumerProps = (Properties)props.clone();
        consumerProps.put("enable.auto.commit", "false");
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

    public Future<RecordMetadata> produce(String topic, TProducerKey key, TProducerValue value,
                                          Iterable<Header> headers, Boolean addNamespace) {
        var record = new ProducerRecord<>(addNamespace ? KafkaHelpers.getNamespacedTopic(topic, namespace) : topic, key, value);
        for (var header : headers) {
            record.headers().add(header);
        }
        return producer.send(record, (event, ex) -> {
            if (ex != null) {
                log.error("Failed to send to Kafka", ex);
            }
        });
    }

    public Future<Object> queueControlMessage(Object obj) {
        return controlMessageQueue.queue(obj);
    }

    @Override
    public void close() {
        producer.close();
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
