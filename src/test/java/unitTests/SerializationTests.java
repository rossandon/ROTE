package unitTests;

import helpers.TestHelpers;
import roteService.tradingEngine.TradingEngineContext;
import roteService.tradingEngine.TradingEngineContextSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializationTests {
    @Test
    public void deserializeContextWithBalances() throws IOException {
        var context = new TradingEngineContext();
        context.adjustBalance(1l, TestHelpers.USD, 10l);
        var bytes = TradingEngineContextSerializer.serialize(context);
        var deserialized = TradingEngineContextSerializer.deserialize(bytes);
        assertEquals(1, deserialized.balances.size());
        assertEquals(1, deserialized.balances.get(1L).size());
        assertEquals(10, deserialized.balances.get(1L).get(TestHelpers.USD.id()));
    }
}
