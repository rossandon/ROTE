package webService.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfigurator implements WebSocketConfigurer {
    @Autowired
    private OrderBookConsumer orderBookConsumer;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(orderBookConsumer, "/market-data/book").setAllowedOrigins("*");
        registry.addHandler(orderBookConsumer, "/market-data/trade").setAllowedOrigins("*");
    }
}
