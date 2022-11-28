package ROTE.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ROTE.utils.UuidHelper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Properties;

@ConfigurationProperties(prefix = "kafka")
public class KafkaConfigurationProvider {
    private String targetHost;
    private String environmentName;
    private String groupId;
    private String uniqueNamespace;
    private String fromStart;

    public String getEnvironmentName() {
        if (environmentName == null && Boolean.parseBoolean(uniqueNamespace)) {
            environmentName = UuidHelper.GetNewUuid();
        }

        return environmentName;
    }

    public void setUniqueNamespace(String uniqueNamespace) {
        this.uniqueNamespace = uniqueNamespace;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setFromStart(String fromStart) {
        this.fromStart = fromStart;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public KafkaConfigurationProvider() {
    }

    public String getGroupId() {
        var builder = new StringBuilder();
        if (environmentName != null) {
            builder.append(environmentName);
        }
        if (groupId != null) {
            builder.append(groupId);
        } else {
            builder.append("default");
        }
        return builder.toString();
    }

    public Properties buildProps() {
        return getConfiguration(targetHost, getGroupId(), Boolean.parseBoolean(fromStart));
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
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ROTE.service");
    }
}
