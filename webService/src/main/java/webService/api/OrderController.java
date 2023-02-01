package webService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.orderBook.OrderBookSide;
import shared.service.TradingEngineKafkaRequestResponseClient;
import shared.service.TradingEngineServiceRequest;

import java.security.Principal;

@RestController
public class OrderController {

    @Autowired
    TradingEngineKafkaRequestResponseClient client;

    @GetMapping("orders/place-order")
    void placeOrder(Principal principal, @RequestParam("instrumentCode") String instrumentCode,
                    @RequestParam("amount") Long amount, @RequestParam("price") Long price,
                    @RequestParam("side") OrderBookSide side) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.limitOrder(amount, price, principal.getName().hashCode(), instrumentCode, side));
        resp.assertOk();
    }
}
