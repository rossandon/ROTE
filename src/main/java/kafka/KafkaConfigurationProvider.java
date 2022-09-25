package kafka;

import org.springframework.beans.factory.annotation.Value;
import utils.UuidHelper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Properties;

public class KafkaConfigurationProvider {
    @Value("${kafkaTargetHost}")
    public String kafkaTargetHost;

    @Value("${environmentName}")
    public String environmentName;

    @Value("${groupId}")
    public String groupId;

    @Value("${kafkaFromStart}")
    public boolean fromStart;

    public String getGroupId() {
        var builder = new StringBuilder();
        if (environmentName != null) {
            builder.append(environmentName);
        }
        if (groupId != null) {
            builder.append(groupId);
        }
        else {
            builder.append("default");
        }
        return builder.toString();
    }

    public Properties buildProps() {
        return getConfiguration(kafkaTargetHost, getGroupId(), fromStart);
    }

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
        props.put("client.id", UuidHelper.GetNewUuid());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "service");
    }
}
