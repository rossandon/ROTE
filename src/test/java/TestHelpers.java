public class TestHelpers {
    public static Asset USD = new Asset("USD");
    public static Asset SPY = new Asset("SPY");
    public static Account testAccount = new Account(0);
    public static Instrument SPYUSD = new Instrument(SPY, USD);
}
