package ROTE.aws;

import ROTE.tradingEngine.TradingEngineContext;
import ROTE.tradingEngine.TradingEngineContextSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;

@Configuration
public class AwsS3TradingEngineContextProvider {
    private final AwsTradingEngineContextConfigurationProvider configurationProvider;

    public AwsS3TradingEngineContextProvider(AwsTradingEngineContextConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    @Bean
    @ConditionalOnProperty(prefix = "tradingEngineContext", name = "provider", havingValue = "s3")
    public TradingEngineContext getAwsS3Context() throws IOException {
        var s3Client = S3Client.builder().build();
        var responseStream = s3Client.getObject(GetObjectRequest.builder().bucket(configurationProvider.getBucket()).key(configurationProvider.getObjectKey()).build());
        var bytes = responseStream.readAllBytes();
        return TradingEngineContextSerializer.deserialize(bytes);
    }
}
