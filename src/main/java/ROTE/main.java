package ROTE;

import ROTE.service.TradingEngineStreamingService;

public class main {
    public static void main(String[] args) {
        var context = RoteService.create();
        var service = context.getBean(TradingEngineStreamingService.class);
        service.run();
    }
}
