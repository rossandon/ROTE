package ROTE.aws;

import ROTE.service.ITradingEngineContextPersistor;
import ROTE.tradingEngine.TradingEngineContext;
import ROTE.tradingEngine.TradingEngineContextSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@ConditionalOnProperty(prefix = "tradingEngineContext", name = "provider", havingValue = "s3")
public class AwsS3TradingEngineContextPersistor implements ITradingEngineContextPersistor {
    private final AwsTradingEngineContextConfigurationProvider configurationProvider;

    public AwsS3TradingEngineContextPersistor(AwsTradingEngineContextConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    @Override
    public void save(TradingEngineContext context) throws JsonProcessingException {
        var bytes = TradingEngineContextSerializer.serialize(context);

        var s3Client = S3Client.builder().build();
        var requestBody = RequestBody.fromBytes(bytes);
        s3Client.putObject(PutObjectRequest.builder().bucket(configurationProvider.getBucket()).key(configurationProvider.getObjectKey()).build(), requestBody);
    }
}
