package integrationTests;

import ROTE.service.TradingEngineServiceConsts;
import ROTE.service.TradingEngineServiceRequest;
import ROTE.tradingEngine.TradingEngineContext;
import ROTE.tradingEngine.TradingEngineContextSerializer;
import ROTE.utils.UuidHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import helpers.IntegrationTest;
import helpers.TestHelpers;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(initializers = ContextProviderTests.AwsTradingEngineContextSeeder.class)
public class ContextProviderTests extends IntegrationTest {

    @Test
    public void Test1() throws Exception {
        var request = TradingEngineServiceRequest.getBalance(TestHelpers.testAccount1.accountId(), TestHelpers.USD.code());
        var response = testClient.send(TradingEngineServiceConsts.RequestTopic, "123", request);
        assertEquals(100, response.getBalanceResult().balance());
    }

    public static class AwsTradingEngineContextSeeder implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        private static TradingEngineContext getContext() {
            var context = new TradingEngineContext();
            context.adjustBalance(TestHelpers.testAccount1.accountId(), TestHelpers.USD, 100);
            return context;
        }

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            try {
                var contents = TradingEngineContextSerializer.serialize(getContext());
                var bucket = configurableApplicationContext.getEnvironment().getProperty("aws.bucket");

                var objectKey = "/contexts/" + UuidHelper.GetNewUuid();
                var s3Client = S3Client.builder().build();
                s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(objectKey).build(), RequestBody.fromBytes(contents));

                TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, "tradingEngineContext.provider=s3");
                TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, "aws.objectKey=" + objectKey);
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
