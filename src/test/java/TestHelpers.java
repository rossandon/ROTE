import kafka.KafkaClient;
import kafka.KafkaConfigurationProvider;
import kafka.KafkaRequestResponseClient;
import referential.InstrumentInventory;
import tradingEngine.Account;
import referential.Asset;
import referential.Instrument;

import java.util.Properties;

public class TestHelpers {
    public static final Asset USD = new Asset("USD");
    public static final Asset SPY = new Asset("SPY");
    public static final Account testAccount1 = new Account(0);
    public static Account testAccount2 = new Account(1);
    public static final Instrument SPYInst = new Instrument(SPY, USD);

    public static InstrumentInventory GetInventory() {
        var inventory = new InstrumentInventory();
        inventory.addInstrument("SPY", SPYInst);
        return inventory;
    }

    public static final String KafkaTestHost = "localhost:9092";

    public static Properties getKafkaConfiguration(String groupId) {
        return KafkaConfigurationProvider.getConfiguration(KafkaTestHost, groupId, true);
    }

    public static <TKey, TValue> KafkaClient getKafkaClient(String groupId, String namespace) {
        return new KafkaClient(namespace, getKafkaConfiguration(groupId));
    }

    public static <TKey, TRequest, TResponse> KafkaRequestResponseClient<TKey, TRequest, TResponse> getKafkaRequestResponseClient(String groupId, String namespace) {
        return new KafkaRequestResponseClient<TKey, TRequest, TResponse>(namespace, getKafkaConfiguration(groupId));
    }
}
