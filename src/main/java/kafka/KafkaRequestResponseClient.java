package kafka;

import utils.AutoResetEvent;
import utils.UuidHelper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class KafkaRequestResponseClient<TKey, TRequest, TResponse> implements Runnable, AutoCloseable {
    private final String namespace;
    private final Properties props;
    private final String consumerId = UuidHelper.GetNewUuid() + "-responses";
    private final KafkaProducer<TKey, TRequest> producer;
    private final ResponseWatcher responseWatcher = new ResponseWatcher();
    public final long timeout = 10_000;

    public KafkaRequestResponseClient(KafkaConfigurationProvider kafkaConfigurationProvider) {
        this.namespace =  kafkaConfigurationProvider.environmentName;
        this.props = kafkaConfigurationProvider.buildProps();
        this.producer = new KafkaProducer<TKey, TRequest>(props);
    }

    public void run() {
        try (var consumer = new KafkaConsumer<TKey, TResponse>(props)) {
            consumer.subscribe(Arrays.asList(consumerId));

            while (true) {
                var results = consumer.poll(Duration.ofSeconds(1));
                for (var result : results) {
                    responseWatcher.handle(result);
                }
            }
        }
    }

    public TResponse send(String topic, TKey key, TRequest request) throws Exception {
        var requestId = UuidHelper.GetNewUuid();
        var autoResetEvent = new AutoResetEvent(false);
        AtomicReference<ConsumerRecord<TKey, TResponse>> responseRecordContainer = new AtomicReference<>();
        try (var watcher = responseWatcher.watch(requestId, r -> {
            responseRecordContainer.set(r);
            autoResetEvent.set();
        })) {
            var requestRecord = new ProducerRecord<TKey, TRequest>(KafkaHelpers.getNamespacedTopic(topic, namespace), key, request);
            requestRecord.headers().add(KafkaConsts.ResponseIdHeader, requestId.getBytes(StandardCharsets.UTF_8));
            requestRecord.headers().add(KafkaConsts.ResponseTopicHeader, consumerId.getBytes(StandardCharsets.UTF_8));
            producer.send(requestRecord);
            autoResetEvent.waitOne(timeout);
            var responseRecord = responseRecordContainer.get();
            if (responseRecord == null)
                throw new TimeoutException("Timeout waiting for response");
            return responseRecord.value();
        }
    }

    @Override
    public void close() {
        producer.close();
    }

    private class ResponseWatcher implements IKafkaConsumerHandler<TKey, TResponse> {
        private final HashMap<String, IKafkaConsumerHandler<TKey, TResponse>> handlers = new HashMap<>();

        @Override
        public void handle(ConsumerRecord<TKey, TResponse> record) {
            synchronized (handlers) {
                var responseId = new String(record.headers().headers(KafkaConsts.ResponseIdHeader).iterator().next().value(), StandardCharsets.UTF_8);
                var handler = handlers.get(responseId);
                if (handler != null) {
                    handler.handle(record);
                    handlers.remove(responseId);
                }
            }
        }

        public AutoCloseable watch(String id, IKafkaConsumerHandler<TKey, TResponse> handler) {
            synchronized (handlers) {
                handlers.put(id, handler);
                return new ResponseWatchEntry(id, this);
            }
        }

        private void remove(String id) {
            synchronized (handlers) {
                handlers.remove(id);
            }
        }

        private class ResponseWatchEntry implements AutoCloseable {
            private final String id;
            private final ResponseWatcher responseWatcher;

            public ResponseWatchEntry(String id, ResponseWatcher responseWatcher) {
                this.id = id;
                this.responseWatcher = responseWatcher;
            }

            @Override
            public void close() {
                responseWatcher.remove(id);
            }
        }
    }
}
