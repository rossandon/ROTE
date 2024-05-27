package tradingEngineService;

import tradingEngineService.referential.Asset;
import tradingEngineService.referential.Instrument;
import tradingEngineService.referential.ReferentialInventory;
import tradingEngineService.service.TradingEngineStreamingService;

public class Main {
    public static void main(String[] args) {
        var context = RoteService.create();
        var service = context.getBean(TradingEngineStreamingService.class);
        var referentialInventory = context.getBean(ReferentialInventory.class);

        var usd = new Asset("USD", 1);
        var spy = new Asset("SPY", 2);
        var btc = new Asset("BTC", 3);
        referentialInventory.addAsset(usd);
        referentialInventory.addAsset(spy);
        referentialInventory.addAsset(btc);
        referentialInventory.addInstrument(new Instrument("SPY", spy, usd, 1));
        referentialInventory.addInstrument(new Instrument("BTC/USD", btc, usd, 2));
    }
}
