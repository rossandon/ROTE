package shared.kafka;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

public class SeekFromEndListener<TValue, TKey> implements ConsumerRebalanceListener {
    private static final Logger log = Logger.getLogger(SeekFromEndListener.class);

    private final KafkaConsumer<TKey, TValue> consumer;
    private final int lookBack;

    public SeekFromEndListener(KafkaConsumer<TKey, TValue> consumer, int lookBack) {
        this.consumer = consumer;
        this.lookBack = lookBack;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        Map<TopicPartition, Long> topicPartitionLongMap;
        topicPartitionLongMap = consumer.endOffsets(partitions);
        topicPartitionLongMap.forEach((partition, offset) ->
        {
            log.info(String.format("Seek %s to %d", partition.topic(), offset));
            consumer.seek(partition, Math.max(offset - lookBack, 0));
        });
    }
}
