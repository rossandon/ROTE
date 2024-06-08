package unitTests;

import helpers.TestHelpers;
import org.junit.jupiter.api.Test;
import tradingEngineService.tradingEngine.TradingEngineContext;
import tradingEngineService.tradingEngine.TradingEngineContextSerializer;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializationTests {
    @Test
    public void deserializeContextWithBalances() throws IOException {
        var context = new TradingEngineContext();
        context.adjustBalance(1L, TestHelpers.USD, BigDecimal.valueOf(10L));
        var bytes = TradingEngineContextSerializer.serialize(context);
        var deserialized = TradingEngineContextSerializer.deserialize(bytes);
        assertEquals(1, deserialized.balances.size());
        assertEquals(1, deserialized.balances.get(1L).size());
        assertEquals(10, deserialized.balances.get(1L).get(TestHelpers.USD.id()).longValue());
    }
}
