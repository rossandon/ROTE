package helpers;

import ROTE.RoteService;
import ROTE.referential.ReferentialInventory;
import ROTE.tradingEngine.Account;
import ROTE.referential.Asset;
import ROTE.referential.Instrument;
import org.springframework.context.ApplicationContext;

public class TestHelpers {
    public static final Asset USD = new Asset("USD", 1);
    public static final Asset SPY = new Asset("SPY", 2);
    public static final Account testAccount1 = new Account(0);
    public static Account testAccount2 = new Account(1);
    public static final Instrument SPYInst = new Instrument("SPY", SPY, USD, 1);

    public static ReferentialInventory ConfigureInventory(ReferentialInventory inventory) {

        return inventory;
    }

    public static ApplicationContext createEnvironment() {
        var context = RoteService.create();
        var instrumentInventory = context.getBean(ReferentialInventory.class);
        ConfigureInventory(instrumentInventory);
        return context;
    }
}
