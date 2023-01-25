package roteShared.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

@FunctionalInterface
public interface IKafkaConsumerHandler<TKey, TValue> {
    void handle(ConsumerRecord<TKey, TValue> result);
}
