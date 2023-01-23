package roteService.aws;

import roteService.tradingEngine.ITradingEngineContextProvider;
import roteService.tradingEngine.TradingEngineContext;
import roteService.tradingEngine.TradingEngineContextSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;

@Component
@ConditionalOnProperty(prefix = "tradingEngineContext", name = "provider", havingValue = "s3")
public class AwsS3TradingEngineContextProvider implements ITradingEngineContextProvider {
    private final AwsTradingEngineContextConfigurationProvider configurationProvider;

    public AwsS3TradingEngineContextProvider(AwsTradingEngineContextConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public TradingEngineContext getContext() {
        var s3Client = S3Client.builder().build();
        var responseStream = s3Client.getObject(GetObjectRequest.builder().bucket(configurationProvider.getBucket()).key(configurationProvider.getObjectKey()).build());
        try {
            var bytes = responseStream.readAllBytes();
            return TradingEngineContextSerializer.deserialize(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}