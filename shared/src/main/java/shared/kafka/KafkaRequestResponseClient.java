package shared.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import shared.utils.AutoResetEvent;
import shared.utils.UuidHelper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class KafkaRequestResponseClient<TKey, TRequest, TResponse> implements Runnable, AutoCloseable {
    private final RoteKafkaProducer<TKey, TRequest> kafkaProducer;
    private final RoteKafkaConsumer kafkaConsumer;
    private final String consumerId = UuidHelper.GetNewUuid() + "-responses";
    private final ResponseWatcher responseWatcher = new ResponseWatcher();
    public final long timeout = 10_000;

    public KafkaRequestResponseClient(RoteKafkaProducer<TKey, TRequest> kafkaProducer, RoteKafkaConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    public void run() {
        kafkaConsumer.consume(consumerId, 0, false, this::handle, object -> {});
    }

    private void handle(ConsumerRecord<TKey,TResponse> result) {
        responseWatcher.handle(result);
    }

    public TResponse send(String topic, TKey key, TRequest request) throws Exception {
        var requestId = UuidHelper.GetNewUuid();
        var autoResetEvent = new AutoResetEvent(false);
        AtomicReference<ConsumerRecord<TKey, TResponse>> responseRecordContainer = new AtomicReference<>();
        try (var watcher = responseWatcher.watch(requestId, r -> {
            responseRecordContainer.set(r);
            autoResetEvent.set();
        })) {
            Header responseIdHeader = new RoteKafkaConsumer.KafkaHeader(KafkaConsts.ResponseIdHeader, requestId.getBytes(StandardCharsets.UTF_8));
            Header responseTopicHeader = new RoteKafkaConsumer.KafkaHeader(KafkaConsts.ResponseTopicHeader, consumerId.getBytes(StandardCharsets.UTF_8));
            var headers = List.of(responseIdHeader, responseTopicHeader);
            kafkaProducer.produce(topic, key, request, headers, true);
            autoResetEvent.waitOne(timeout);
            var responseRecord = responseRecordContainer.get();
            if (responseRecord == null)
                throw new TimeoutException("Timeout waiting for response");
            return responseRecord.value();
        }
    }

    @Override
    public void close() {
        kafkaConsumer.close();
        kafkaConsumer.close();
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
