package shared.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import shared.utils.UuidHelper;
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
    private String tls;

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

    public void setTargetHost(String targetHost) { this.targetHost = targetHost; }

    public void setTls(String tls) { this.tls = tls; }

    public KafkaConfigurationProvider() {
    }

    public Properties buildProducerProps() {
        var props = getCoreProps();
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");
        return props;
    }

    public Properties buildConsumerProps() {
        var props = getCoreProps();
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
        props.put("auto.offset.reset", Boolean.parseBoolean(fromStart) ? "earliest" : "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "shared.service");
        return props;
    }

    private Properties getCoreProps() {
        var props = new Properties();
        props.put("bootstrap.servers", targetHost);
        props.put("client.id", UuidHelper.GetNewUuid());
        if (Boolean.parseBoolean(tls))
            props.put("security.protocol", "SSL");
        return props;
    }

    public Properties buildAdminProps() {
        var props = new Properties();
        props.put("bootstrap.servers", targetHost);
        if (Boolean.parseBoolean(tls))
            props.put("security.protocol", "SSL");
        return props;
    }

    private String getGroupId() {
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
}
