package webService.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;
import shared.kafka.RoteKafkaConsumer;
import shared.service.results.OrderBookSnapshot;
import shared.service.results.Trade;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static shared.service.TradingEngineServiceConsts.TradeDataTopic;

@Component
public class TradeTapeConsumer extends AbstractWebSocketHandler implements Runnable {
    private static final Logger log = Logger.getLogger(OrderBookConsumer.class);

    private final RoteKafkaConsumer roteKafkaConsumer;
    private final HashMap<String, List<WebSocketSession>> webSocketConnections = new HashMap<>();

    public TradeTapeConsumer(RoteKafkaConsumer roteKafkaConsumer) {
        this.roteKafkaConsumer = roteKafkaConsumer;
    }

    @Override
    public void run() {
        roteKafkaConsumer.consumeFromEnd(TradeDataTopic, 10, (ConsumerRecord<String, Trade> result) -> {
            var snapshot = result.value();
            List<WebSocketSession> sessions;
            synchronized (webSocketConnections) {
                var listOrNull = webSocketConnections.get(snapshot.instrumentCode());
                if (listOrNull == null) {
                    return;
                }
                sessions = new ArrayList<>(listOrNull);
            }
            for (var session : sessions) {
                sendTrade(session, snapshot);
            }
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        var instrumentCode = getSubscribedInstrumentCode(session);
        synchronized (webSocketConnections) {
            var entries = webSocketConnections.computeIfAbsent(instrumentCode, a -> new ArrayList<>());
            entries.add(session);
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
