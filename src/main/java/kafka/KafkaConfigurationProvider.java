package kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Properties;

public class KafkaConfigurationProvider {
    public static Properties getConfiguration(String targetHost, String groupId, Boolean fromStart) {
        var props = getConfiguration(targetHost, fromStart);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return props;
    }

    public static Properties getConfiguration(String targetHost, Boolean fromStart) {
        var props = new Properties();
        props.put("bootstrap.servers", targetHost);
        props.put("auto.offset.reset", fromStart ? "earliest" : "latest");
        getConfiguration(props);
        return props;
    }

    public static void getConfiguration(Properties props) {
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "service");
    }
}
