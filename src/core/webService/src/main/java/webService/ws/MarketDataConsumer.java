package webService.ws;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import shared.kafka.RoteKafkaConsumer;

import static shared.service.TradingEngineServiceConsts.MarketDataTopic;

@Component
public class MarketDataConsumer implements Runnable {
    private static final Logger log = Logger.getLogger(MarketDataConsumer.class);

    private final RoteKafkaConsumer roteKafkaConsumer;

    public MarketDataConsumer(RoteKafkaConsumer roteKafkaConsumer) {

        this.roteKafkaConsumer = roteKafkaConsumer;
    }

    @Override
    public void run() {
        roteKafkaConsumer.consumeFromEnd(MarketDataTopic, 1, result -> {
        });
    }
}
