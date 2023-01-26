package shared.kafka;

import shared.utils.ProcessingQueue;

@FunctionalInterface
public interface IKafkaControlMessageHandler {
    void handle(ProcessingQueue.ProcessingQueueItem object);
}
