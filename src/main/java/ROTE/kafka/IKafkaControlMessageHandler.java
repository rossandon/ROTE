package ROTE.kafka;

import ROTE.utils.ProcessingQueue;

@FunctionalInterface
public interface IKafkaControlMessageHandler {
    void handle(ProcessingQueue.ProcessingQueueItem object);
}
