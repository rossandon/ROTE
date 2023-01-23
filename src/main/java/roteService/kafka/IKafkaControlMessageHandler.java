package roteService.kafka;

import roteService.utils.ProcessingQueue;

@FunctionalInterface
public interface IKafkaControlMessageHandler {
    void handle(ProcessingQueue.ProcessingQueueItem object);
}
