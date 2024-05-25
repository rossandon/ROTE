package helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import shared.service.TradingEngineKafkaRequestResponseClient;
import shared.service.TradingEngineServiceConsts;
import shared.service.TradingEngineServiceRequest;
import shared.service.TradingEngineServiceResponse;
import tradingEngineService.RoteService;

@SpringBootTest(classes = RoteService.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({TestReferentialInitializer.class, TestKafkaRequestResponseClientExecutor.class})
public class IntegrationTest {
    @Autowired
    protected TradingEngineKafkaRequestResponseClient testClient;


    protected TradingEngineServiceResponse send(TradingEngineServiceRequest request) throws Exception {
        return testClient.send(TradingEngineServiceConsts.WriteRequestTopic, "", request);
    }
}
