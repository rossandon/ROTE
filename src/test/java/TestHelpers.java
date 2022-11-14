import ROTE.RoteService;
import ROTE.kafka.KafkaClient;
import ROTE.kafka.KafkaConfigurationProvider;
import ROTE.kafka.KafkaRequestResponseClient;
import ROTE.referential.InstrumentInventory;
import ROTE.tradingEngine.Account;
import ROTE.referential.Asset;
import ROTE.referential.Instrument;
import org.springframework.context.ApplicationContext;

public class TestHelpers {
    public static final Asset USD = new Asset("USD");
    public static final Asset SPY = new Asset("SPY");
    public static final Account testAccount1 = new Account(0);
    public static Account testAccount2 = new Account(1);
    public static final Instrument SPYInst = new Instrument(SPY, USD);

    public static InstrumentInventory ConfigureInventory(InstrumentInventory inventory) {
        inventory.addInstrument("SPY", SPYInst);
        return inventory;
    }

    public static ApplicationContext createEnvironment() {
        var context = RoteService.create();
        var instrumentInventory = context.getBean(InstrumentInventory.class);
        ConfigureInventory(instrumentInventory);
        return context;
    }
}
