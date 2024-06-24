package webService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.service.TradingEngineKafkaRequestResponseClient;
import shared.service.TradingEngineServiceRequest;
import webService.security.RoteUserContext;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.concurrent.Future;

@RestController
public class BalanceController {

    @Autowired
    TradingEngineKafkaRequestResponseClient client;

    @GetMapping("balances/list")
    Future<HashMap<String, BigDecimal>> list() throws Exception {
        var future = client.sendAsync(TradingEngineServiceRequest.getBalances(RoteUserContext.GetAccountId()));
        return future.thenApply(resp -> {
            try {
                resp.assertOk();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return resp.getBalancesResult().balances();
        });
    }

    @PostMapping("balances/deposit")
    void deposit(@RequestParam("assetCode") String assetCode, @RequestParam("amount") BigDecimal amount) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.adjustBalance(amount, RoteUserContext.GetAccountId(), assetCode));
        resp.assertOk();
    }
}
