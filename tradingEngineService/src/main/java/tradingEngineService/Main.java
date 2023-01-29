package tradingEngineService;

import tradingEngineService.referential.Asset;
import tradingEngineService.referential.ReferentialInventory;
import tradingEngineService.service.TradingEngineStreamingService;

public class Main {
    public static void main(String[] args) {
        var context = RoteService.create();
        var service = context.getBean(TradingEngineStreamingService.class);
        var referentialInventory = context.getBean(ReferentialInventory.class);
        referentialInventory.addAsset(new Asset("USD", 1));
        referentialInventory.addAsset(new Asset("SPY", 2));
    }
}
