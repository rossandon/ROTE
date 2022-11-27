package ROTE.tradingEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TradingEngineContextSerializer {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static TradingEngineContext deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, TradingEngineContext.class);
    }

    public static byte[] serialize(TradingEngineContext tradingEngineContext) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(tradingEngineContext);
    }
}
