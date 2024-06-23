package tradingEngineService.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import shared.kafka.*;
import shared.service.TradingEngineServiceConsts;
import shared.service.TradingEngineServiceRequest;
import shared.service.TradingEngineServiceRequestType;
import shared.service.TradingEngineServiceResponse;
import shared.service.results.OrderBookSnapshot;
import shared.service.results.Trade;
import shared.service.results.TradingEngineErrorResult;
import shared.utils.ProcessingQueue;
import tradingEngineService.referential.ReferentialInventory;
import tradingEngineService.tradingEngine.TradingEngine;
import tradingEngineService.tradingEngine.TradingEngineContextInstance;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@Component
public class TradingEngineConsumer implements AutoCloseable {
    private static final Logger log = Logger.getLogger(TradingEngineStreamingService.class);

    private final RoteKafkaProducer<String, TradingEngineServiceResponse> kafkaProducer;
    private final KafkaConfigurationProvider kafkaConfigurationProvider;
    private final TradingEngineContextInstance tradingEngineContextInstance;
    private final ITradingEngineContextPersistor tradingContextPersistor;
    private final HashMap<TradingEngineServiceRequestType, ITradingEngineRequestHandler> handlers = new HashMap<>();
    private final ProcessingQueue controlMessageQueue = new ProcessingQueue();
    private boolean closed;

    public TradingEngineConsumer(RoteKafkaProducer<String, TradingEngineServiceResponse> responseProducer, RoteKafkaProducer<String, OrderBookSnapshot> orderBookSnapshotProducer, RoteKafkaProducer<String, Trade> tradeProducer, KafkaConfigurationProvider kafkaConfigurationProvider, TradingEngine tradingEngine, ReferentialInventory referentialInventory, TradingEngineContextInstance tradingEngineContextInstance, ITradingEngineContextPersistor tradingContextPersistor) {
        this.kafkaProducer = responseProducer;
        this.kafkaConfigurationProvider = kafkaConfigurationProvider;
        this.tradingEngineContextInstance = tradingEngineContextInstance;
        this.tradingContextPersistor = tradingContextPersistor;
    }

    public void addHandler() {

    }

    public void run() {
        try {
            try (var adminClient = new RoteKafkaAdminClient(this.kafkaConfigurationProvider)) {
                adminClient.createTopic(TradingEngineServiceConsts.WriteRequestTopic, 1);
                adminClient.createTopic(TradingEngineServiceConsts.OrderBookDataTopic, 1);
                adminClient.createTopic(TradingEngineServiceConsts.TradeDataTopic, 1);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        var startingOffset = tradingEngineContextInstance.getContext().sequence;
        try {
            log.info("Running trading engine service");
            consume(startingOffset);
            log.info("Stopped trading engine service");
        } catch (Exception e) {
            log.error("Trading engine service quit", e);
            throw e;
        }
    }

    private void consume(long startingOffset) {
        var consumerProps = (Properties) kafkaConfigurationProvider.buildConsumerProps().clone();
        var namespace = kafkaConfigurationProvider.getEnvironmentName();
        consumerProps.put("enable.auto.commit", Boolean.toString(false).toLowerCase());
        try (var consumer = new KafkaConsumer<String, TradingEngineServiceRequest>(consumerProps)) {

            var topic = TradingEngineServiceConsts.WriteRequestTopic;
            var topicPartition = new TopicPartition(KafkaHelpers.getNamespacedTopic(topic, namespace), 0);
            var initialPartitionOffset = consumer.endOffsets(List.of(topicPartition)).get(topicPartition);

            consumer.assign(List.of(topicPartition));
            consumer.seek(topicPartition, startingOffset);

            log.info(String.format("Trading engine offsets: snapshot = %d, end = %d, diff = %d", startingOffset, initialPartitionOffset, initialPartitionOffset - startingOffset));

            while (!closed) {
                var results = consumer.poll(Duration.ofSeconds(1));
                for (var result : results) {
                    if (initialPartitionOffset == result.offset()) {
                        log.info("Finished processing trading engine backlog");
                    }

                    handleKafkaRecord(result);
                }

                while (true) {
                    var item = controlMessageQueue.dequeue();
                    if (item == null) break;
                    log.debug("Processing control message");
                    handleControlMessage(item);
                    log.debug("Processed control message");
                }
            }
        }
    }

    @Override
    public void close() {
        closed = true;
    }

    public void addHandler(TradingEngineServiceRequestType tradingEngineServiceRequestType, ITradingEngineRequestHandler handler) {
        handlers.put(tradingEngineServiceRequestType, handler);
    }

    public record TopicPartitionAndOffet(String topic, int partition, Optional<Long> offset) {
    }

    public Future<Object> snapshot() {
        return controlMessageQueue.queue(new TradingEngineServiceSnapshotRequest());
    }

    private void handleControlMessage(ProcessingQueue.ProcessingQueueItem o) {
        try {
            if (o.getObject() instanceof TradingEngineServiceSnapshotRequest) {
                var context = tradingEngineContextInstance.getContext();
                tradingContextPersistor.save(context);
                o.setResult(true);
            } else throw new Exception("Unknown control message type");
        } catch (Exception e) {
            log.error("Failed to process control message", e);
            o.setException(e);
        }
    }

    private void handleKafkaRecord(ConsumerRecord<String, TradingEngineServiceRequest> record) {
        var request = record.value();
        var responseTopicBytes = record.headers().headers(KafkaConsts.ResponseTopicHeader).iterator().next().value();
        var responseIdBytes = record.headers().headers(KafkaConsts.ResponseIdHeader).iterator().next().value();
        var responseTopic = new String(responseTopicBytes, StandardCharsets.UTF_8);
        var responseId = new String(responseIdBytes, StandardCharsets.UTF_8);

        TradingEngineServiceResponse response;
        try {
            var type = request.type();
            log.debug("Processing '" + type + "' request");
            var handler = handlers.get(type);
            response = handler.handle(request);
            tradingEngineContextInstance.getContext().sequence = record.offset();
        } catch (Exception e) {
            log.error("Failed to process request", e);
            response = new TradingEngineServiceResponse(new TradingEngineErrorResult(e.getMessage()));
        }

        sendResponse(responseTopic, responseId, response);
    }


    private void sendResponse(String responseTopic, String responseId, TradingEngineServiceResponse response) {
        Header responseIdHeader = new RoteKafkaConsumer.KafkaHeader(KafkaConsts.ResponseIdHeader, responseId.getBytes(StandardCharsets.UTF_8));
        var headers = List.of(responseIdHeader);
        kafkaProducer.produce(responseTopic, null, response, headers, true);
    }

}
