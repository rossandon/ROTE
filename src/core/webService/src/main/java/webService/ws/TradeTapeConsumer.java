package webService.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;
import shared.kafka.KafkaConfigurationProvider;
import shared.kafka.RoteKafkaAdminClient;
import shared.kafka.RoteKafkaConsumer;
import shared.service.TradingEngineServiceConsts;
import shared.service.results.Trade;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static shared.service.TradingEngineServiceConsts.TradeDataTopic;

@Component
public class TradeTapeConsumer extends AbstractWebSocketHandler implements Runnable {
    private static final Logger log = Logger.getLogger(OrderBookConsumer.class);

    private final RoteKafkaConsumer roteKafkaConsumer;
    private final KafkaConfigurationProvider kafkaConfigurationProvider;
    private final HashMap<String, List<Trade>> recent = new HashMap<>();

    private final HashMap<String, List<WebSocketSession>> webSocketConnections = new HashMap<>();

    public TradeTapeConsumer(RoteKafkaConsumer roteKafkaConsumer, KafkaConfigurationProvider kafkaConfigurationProvider) {
        this.roteKafkaConsumer = roteKafkaConsumer;
        this.kafkaConfigurationProvider = kafkaConfigurationProvider;
    }

    @Override
    public void run() {
        try {
            try (var adminClient = new RoteKafkaAdminClient(this.kafkaConfigurationProvider)) {
                adminClient.createTopic(TradingEngineServiceConsts.TradeDataTopic, 1);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        roteKafkaConsumer.consumeFromEnd(TradeDataTopic, 10, (ConsumerRecord<String, Trade> result) -> {
            var snapshot = result.value();
            if (recordToRecentTrades(snapshot)) {
                broadcastTradeToAllConnections(snapshot);
            }
        });
    }

    private void broadcastTradeToAllConnections(Trade snapshot) {
        var instrumentCode = snapshot.instrumentCode();
        List<WebSocketSession> sessions;
        synchronized (webSocketConnections) {
            var listOrNull = webSocketConnections.get(instrumentCode);
            if (listOrNull == null) {
                return;
            }
            sessions = new ArrayList<>(listOrNull);
        }
        for (var session : sessions) {
            sendTrade(session, snapshot);
        }
    }

    private boolean recordToRecentTrades(Trade snapshot) {
        var instrumentCode = snapshot.instrumentCode();

        synchronized (recent) {
            var existing = recent.computeIfAbsent(instrumentCode, l -> new ArrayList<>());
            if (!existing.isEmpty()) {
                var mostRecent = existing.get(existing.size() - 1);
                if (mostRecent.id() >= snapshot.id()) {
                    return false;
                }
            }

            existing.add(snapshot);
            if (existing.size() > 10) {
                existing.remove(0);
            }

            return true;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        var instrumentCode = getSubscribedInstrumentCode(session);
        synchronized (webSocketConnections) {
            var entries = webSocketConnections.computeIfAbsent(instrumentCode, a -> new ArrayList<>());
            entries.add(session);
        }
        List<Trade> tradesToSend = null;
        synchronized (recent) {
            var recentTrades = recent.get(instrumentCode);
            if (recentTrades != null) {
                tradesToSend = new ArrayList<>(recentTrades);
            }
        }
        if (tradesToSend != null) {
            for (var trade : tradesToSend) {
                sendTrade(session, trade);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        var instrumentCode = getSubscribedInstrumentCode(session);
        synchronized (webSocketConnections) {
            var entries = webSocketConnections.computeIfAbsent(instrumentCode, a -> new ArrayList<>());
            entries.remove(session);
        }
    }

    private String getSubscribedInstrumentCode(WebSocketSession session) {
        var components = UriComponentsBuilder.fromUri(session.getUri()).build();
        var instrumentCode = components.getQueryParams().get("instrumentCode");
        return instrumentCode.get(0);
    }

    private void sendTrade(WebSocketSession session, Trade orderBookSnapshot) {
        try {
            StringWriter writer = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, orderBookSnapshot);
            String jsonText = writer.toString();
            session.sendMessage(new TextMessage(jsonText));
        } catch (IOException e) {
            log.error("failed to send trade", e);
        }
    }
}
