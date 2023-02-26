package shared.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.log4j.Logger;
import shared.utils.UuidHelper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class KafkaRequestResponseClient<TKey, TRequest, TResponse> implements Runnable, AutoCloseable {
    private static final Logger log = Logger.getLogger(KafkaRequestResponseClient.class);

    private final RoteKafkaProducer<TKey, TRequest> kafkaProducer;
    private final RoteKafkaConsumer kafkaConsumer;
    private final String consumerId = UuidHelper.GetNewUuid() + "-responses";
    private final ResponseWatcher responseWatcher = new ResponseWatcher();
    private final RoteKafkaAdminClient kafkaAdminClient;
    private final CompletableFuture<Object> initialized = new CompletableFuture<>();
    public final long timeout = 10;


    public KafkaRequestResponseClient(RoteKafkaProducer<TKey, TRequest> kafkaProducer,
                                      RoteKafkaConsumer kafkaConsumer,
                                      RoteKafkaAdminClient kafkaAdminClient) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaAdminClient = kafkaAdminClient;
    }

    public void run() {
        try {
            kafkaAdminClient.createTopic(consumerId, 1);
            initialized.complete(null);
            kafkaConsumer.consumePartition(consumerId, 0, 0, false, this::handle);
        }
        catch (Exception e) {
            log.error("Error running request-response client", e);
            initialized.completeExceptionally(e);
            throw new RuntimeException(e);
        }
    }

    private void handle(ConsumerRecord<TKey, TResponse> result) {
        responseWatcher.handle(result);
    }

    public TResponse send(String topic, TKey key, TRequest request) throws Exception {
        return sendAsync(topic, key, request).get(30, TimeUnit.SECONDS);
    }

    public CompletableFuture<TResponse> sendAsync(String topic, TKey key, TRequest request) throws Exception {
        waitInitialized();

        var future = new CompletableFuture<TResponse>();
        var requestId = UuidHelper.GetNewUuid();
        var watcher = responseWatcher.watch(requestId, r -> future.complete(r.value()));
        var responseIdHeader = (Header)new RoteKafkaConsumer.KafkaHeader(KafkaConsts.ResponseIdHeader, requestId.getBytes(StandardCharsets.UTF_8));
        var responseTopicHeader = (Header)new RoteKafkaConsumer.KafkaHeader(KafkaConsts.ResponseTopicHeader, consumerId.getBytes(StandardCharsets.UTF_8));
        var headers = List.of(responseIdHeader, responseTopicHeader);
        kafkaProducer.produce(topic, key, request, headers, true);

        future.handle((r, e) -> {
            try {
                watcher.close();
            } catch (Exception ignored) { }
            return 0;
        });

        return future;
    }

    private void waitInitialized() throws InterruptedException, ExecutionException, TimeoutException {
        initialized.get(30, TimeUnit.SECONDS);
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
            IKafkaConsumerHandler<TKey, TResponse> handler;
            synchronized (handlers) {
                var responseId = new String(record.headers().headers(KafkaConsts.ResponseIdHeader).iterator().next().value(), StandardCharsets.UTF_8);
                handler = handlers.get(responseId);
                if (handler != null) {
                    handlers.remove(responseId);
                }
            }

            if (handler != null)
                handler.handle(record);
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
