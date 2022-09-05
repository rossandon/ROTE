package kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaClient implements AutoCloseable {
    private final String namespace;
    private final Properties props;
    private final KafkaProducer producer;

    public KafkaClient(String namespace, Properties props) {
        this.namespace = namespace;
        this.props = props;
        this.producer = new KafkaProducer<>(props);
    }

    public <TKey, TValue> void consume(List<String> topics, IKafkaConsumerHandler<TKey, TValue> handler) {
        try (var consumer = new KafkaConsumer<TKey, TValue>(props)) {
            consumer.subscribe(KafkaHelpers.getNamespacedTopics(topics, namespace));

            while (true) {
                var results = consumer.poll(Duration.ofSeconds(1));
                for (var result : results) {
                    handler.handle(result);
                }
            }
        }
    }

    public <TKey, TValue> Future<RecordMetadata> produce(String topic, TKey key, TValue value, Iterable<Header> headers, Boolean addNamespace) {
        var record = new ProducerRecord<>(addNamespace ? KafkaHelpers.getNamespacedTopic(topic, namespace) : topic, key, value);
        for (var header : headers) {
            record.headers().add(header);
        }
        return producer.send(record,
                (event, ex) -> {
                    if (ex != null)
                        ex.printStackTrace();
                    else
                        System.out.printf("Produced event to topic %s: key = %-10s value = %s%n", topic, key, value);
                });
    }

    @Override
    public void close() {
        producer.close();
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
