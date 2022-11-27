package helpers;

import ROTE.RoteService;
import ROTE.kafka.KafkaRequestResponseClient;
import ROTE.service.TradingEngineServiceRequest;
import ROTE.service.TradingEngineServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = RoteService.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({TestReferentialInitializer.class, KafkaRequestResponseClientExecutor.class})
public class IntegrationTest {
    @Autowired
    protected KafkaRequestResponseClient<String, TradingEngineServiceRequest, TradingEngineServiceResponse> testClient;
}
