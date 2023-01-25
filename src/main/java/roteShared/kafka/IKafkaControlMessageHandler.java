package roteShared.kafka;

import roteUtils.ProcessingQueue;

@FunctionalInterface
public interface IKafkaControlMessageHandler {
    void handle(ProcessingQueue.ProcessingQueueItem object);
}
