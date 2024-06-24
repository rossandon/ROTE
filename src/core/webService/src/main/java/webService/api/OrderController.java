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
import webService.security.RoteUserContext;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
public class OrderController {

    @Autowired
    TradingEngineKafkaRequestResponseClient client;

    @PostMapping("orders/submit")
    void placeOrder(@RequestParam("instrumentCode") String instrumentCode,
                    @RequestParam("amount") BigDecimal amount, @RequestParam("price") BigDecimal price,
                    @RequestParam("side") OrderBookSide side) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.limitOrder(amount, price, RoteUserContext.GetAccountId(), instrumentCode, side));
        resp.assertOk();
        if (resp.limitOrderResult().type() == LimitOrderResultStatus.Rejected) {
            throw new Exception("Rejected: " + resp.limitOrderResult().rejectReason());
        }
    }

    @PostMapping("orders/cancel")
    void cancelOrder(@RequestParam("id") Long id, @RequestParam("instrumentCode") String instrumentCode) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.cancel(RoteUserContext.GetAccountId(), instrumentCode, id));
        resp.assertOk();
    }

    @PostMapping("orders/cancel-all")
    void cancelAllOrders(@RequestParam("instrumentCode") String instrumentCode) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.cancelAll(RoteUserContext.GetAccountId(), instrumentCode));
        resp.assertOk();
    }
}
