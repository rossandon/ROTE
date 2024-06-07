package webService.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;
import shared.kafka.RoteKafkaConsumer;
import shared.service.results.OrderBookSnapshot;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static shared.service.TradingEngineServiceConsts.MarketDataTopic;

@Configuration
@EnableWebSocket
public class MarketDataConsumer implements Runnable, WebSocketConfigurer, WebSocketHandler {
    private static final Logger log = Logger.getLogger(MarketDataConsumer.class);

    private final RoteKafkaConsumer roteKafkaConsumer;
    private final HashMap<String, OrderBookSnapshot> snapshots = new HashMap<>();
    private final HashMap<String, List<WebSocketSession>> webSocketConnections = new HashMap<>();

    public MarketDataConsumer(RoteKafkaConsumer roteKafkaConsumer) {
        this.roteKafkaConsumer = roteKafkaConsumer;
    }

    @Override
    public void run() {
        roteKafkaConsumer.consumeFromEnd(MarketDataTopic, 1, (ConsumerRecord<String, OrderBookSnapshot> result) -> {
            var snapshot = result.value();
            synchronized (snapshots) {
                snapshots.put(snapshot.instrumentCode(), snapshot);
            }
            List<WebSocketSession> sessions;
            synchronized (webSocketConnections) {
                var listOrNull = webSocketConnections.get(snapshot.instrumentCode());
                if (listOrNull == null) {
                    return;
                }
                sessions = new ArrayList<>(listOrNull);
            }
            for (var session : sessions) {
                sendBookSnapshot(session, snapshot);
            }
        });
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(this, "/market-data").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var instrumentCode = getSubscribedInstrumentCode(session);
        synchronized (webSocketConnections) {
            var entries = webSocketConnections.computeIfAbsent(instrumentCode, a -> new ArrayList<>());
            entries.add(session);
        }

        OrderBookSnapshot latestBookSnapshot;
        synchronized (snapshots) {
            latestBookSnapshot = snapshots.get(instrumentCode);
        }
        if (latestBookSnapshot != null) {
            sendBookSnapshot(session, latestBookSnapshot);
        }
    }

    private String getSubscribedInstrumentCode(WebSocketSession session) {
        var components = UriComponentsBuilder.fromHttpUrl(session.getUri().toString()).build();
        var instrumentCode = components.getQueryParams().get("instrumentCode");
        return instrumentCode.get(0);
    }

    private void sendBookSnapshot(WebSocketSession session, OrderBookSnapshot orderBookSnapshot) {
        try {
            StringWriter writer = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, orderBookSnapshot);
            String jsonText = writer.toString();
            session.sendMessage(new TextMessage(jsonText));
        } catch (IOException e) {
            log.error("failed to send book snapshot", e);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("yo");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("websocket error", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("connection closed");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
