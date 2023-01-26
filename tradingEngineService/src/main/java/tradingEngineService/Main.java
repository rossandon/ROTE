package tradingEngineService;

import tradingEngineService.service.TradingEngineStreamingService;

public class Main {
    public static void main(String[] args) {
        var context = RoteService.create();
        var service = context.getBean(TradingEngineStreamingService.class);
        service.run();
    }
}
