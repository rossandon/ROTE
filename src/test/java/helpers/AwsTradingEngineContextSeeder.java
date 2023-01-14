package helpers;

import ROTE.tradingEngine.TradingEngineContext;
import ROTE.tradingEngine.TradingEngineContextSerializer;
import ROTE.utils.UuidHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class AwsTradingEngineContextSeeder implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    protected void seedContext(TradingEngineContext tradingEngineContext) {
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        try {
            var context = new TradingEngineContext();
            seedContext(context);
            var contents = TradingEngineContextSerializer.serialize(context);
            var bucket = configurableApplicationContext.getEnvironment().getProperty("aws.bucket");

            var objectKey = "/contexts/" + UuidHelper.GetNewUuid();
            var s3Client = S3Client.builder().build();
            s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(objectKey).build(), RequestBody.fromBytes(contents));

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, "tradingEngineContext.provider=s3");
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, "aws.objectKey=" + objectKey);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}