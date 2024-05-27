package webService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.orderBook.LimitOrderResultStatus;
import shared.orderBook.OrderBookSide;
import shared.service.TradingEngineKafkaRequestResponseClient;
import shared.service.TradingEngineServiceRequest;

import java.security.Principal;

@RestController
public class OrderController {

    @Autowired
    TradingEngineKafkaRequestResponseClient client;

    @PostMapping("orders/submit")
    void placeOrder(Principal principal, @RequestParam("instrumentCode") String instrumentCode,
                    @RequestParam("amount") Long amount, @RequestParam("price") Long price,
                    @RequestParam("side") OrderBookSide side) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.limitOrder(amount, price, principal.getName().hashCode(), instrumentCode, side));
        resp.assertOk();
        if (resp.limitOrderResult().type() == LimitOrderResultStatus.Rejected) {
            throw new Exception("Rejected");
        }
    }

    @PostMapping("orders/cancel")
    void cancelOrder(Principal principal, @RequestParam("id") Long id, @RequestParam("instrumentCode") String instrumentCode) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.cancel(principal.getName().hashCode(), instrumentCode, id));
        resp.assertOk();
    }
}
