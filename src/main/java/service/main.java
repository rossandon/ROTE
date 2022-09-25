package service;

import kafka.KafkaClient;
import kafka.KafkaConfigurationProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import referential.InstrumentInventory;
import tradingEngine.TradingEngine;
import tradingEngine.TradingEngineContext;

public class main {
    public static void main(String[] args) {
        var kafkaHost = System.getenv("ROTE_KafkaHost");
        if (kafkaHost == null)
            kafkaHost = "localhost:9092";
        var props = KafkaConfigurationProvider.getConfiguration(kafkaHost, "default", false);

        var context = new AnnotationConfigApplicationContext();
        context.register(TradingEngineStreamingService.class, TradingEngine.class, TradingEngineContext.class, InstrumentInventory.class);
        context.registerBean(KafkaClient.class, "default", props);
        context.refresh();

        var service = context.getBean(TradingEngineStreamingService.class);
        service.run();
    }
}
