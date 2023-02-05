package shared.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class RoteKafkaProducer<TProducerKey, TProducerValue> implements AutoCloseable {
    private static final Logger log = Logger.getLogger(RoteKafkaConsumer.class);

    private final String namespace;
    private final KafkaProducer<TProducerKey, TProducerValue> producer;

    public RoteKafkaProducer(KafkaConfigurationProvider kafkaConfigurationProvider) {
        var props = kafkaConfigurationProvider.buildProps();
        this.namespace = kafkaConfigurationProvider.getEnvironmentName();
        this.producer = new KafkaProducer<>(props);
    }

    public Future<RecordMetadata> produce(String topic, TProducerKey key, TProducerValue value,
                                          Iterable<Header> headers, Boolean addNamespace) {
        var record = new ProducerRecord<>(addNamespace ? KafkaHelpers.getNamespacedTopic(topic, namespace) : topic, key, value);

        if (headers != null) {
            for (var header : headers) {
                record.headers().add(header);
            }
        }
        
        return producer.send(record, (event, ex) -> {
            if (ex != null) {
                log.error("Failed to send to Kafka", ex);
            }
        });
    }

    @Override
    public void close() {
        producer.close();
    }
}
