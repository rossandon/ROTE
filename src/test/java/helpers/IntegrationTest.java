package helpers;

import roteService.RoteService;
import roteShared.service.TradingEngineServiceConsts;
import roteShared.service.TradingEngineKafkaRequestResponseClient;
import roteShared.service.TradingEngineServiceRequest;
import roteShared.service.TradingEngineServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = RoteService.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({TestReferentialInitializer.class, TestKafkaRequestResponseClientExecutor.class})
public class IntegrationTest {
    @Autowired
    protected TradingEngineKafkaRequestResponseClient testClient;


    protected TradingEngineServiceResponse send(TradingEngineServiceRequest request) throws Exception {
        return testClient.send(TradingEngineServiceConsts.RequestTopic, "", request);
    }
}
