package roteService.kafka;

import java.util.Collection;
import java.util.List;

public class KafkaHelpers {
    public static String getNamespacedTopic(String topic, String namespace) {
        return namespace + "-" + topic;
    }

    public static List<String> getNamespacedTopics(Collection<String> topic, String namespace) {
        return topic.stream().map(t -> getNamespacedTopic(t, namespace)).toList();
    }
}
