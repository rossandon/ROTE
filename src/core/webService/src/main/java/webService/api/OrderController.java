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

import java.security.Principal;

@RestController
public class OrderController {

    @Autowired
    TradingEngineKafkaRequestResponseClient client;

    @PostMapping("orders/submit")
    void placeOrder(RoteUserContext roteUserContext, @RequestParam("instrumentCode") String instrumentCode,
                    @RequestParam("amount") Long amount, @RequestParam("price") Long price,
                    @RequestParam("side") OrderBookSide side) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.limitOrder(amount, price, roteUserContext.getAccountId(), instrumentCode, side));
        resp.assertOk();
        if (resp.limitOrderResult().type() == LimitOrderResultStatus.Rejected) {
            throw new Exception("Rejected: " + resp.limitOrderResult().rejectReason());
        }
    }

    @PostMapping("orders/cancel")
    void cancelOrder(RoteUserContext roteUserContext, @RequestParam("id") Long id, @RequestParam("instrumentCode") String instrumentCode) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.cancel(roteUserContext.getAccountId(), instrumentCode, id));
        resp.assertOk();
    }

    @PostMapping("orders/cancel-all")
    void cancelAllOrders(RoteUserContext roteUserContext, @RequestParam("instrumentCode") String instrumentCode) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.cancelAll(roteUserContext.getAccountId(), instrumentCode));
        resp.assertOk();
    }
}