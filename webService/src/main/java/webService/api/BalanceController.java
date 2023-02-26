package webService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.service.TradingEngineKafkaRequestResponseClient;
import shared.service.TradingEngineServiceRequest;

import java.security.Principal;
import java.util.HashMap;
import java.util.concurrent.Future;

@RestController
public class BalanceController {

    @Autowired
    TradingEngineKafkaRequestResponseClient client;

    @GetMapping("balances/list")
    Future<HashMap<String, Long>> listBalances(Principal principal) throws Exception {
        var future = client.sendAsync(TradingEngineServiceRequest.getBalances(principal.getName().hashCode()));
        return future.thenApply(resp -> {
            try {
                resp.assertOk();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return resp.getBalancesResult().balances();
        });
    }

    @GetMapping("balances/add")
    void addBalances(Principal principal, @RequestParam("assetCode") String assetCode, @RequestParam("amount") Long amount) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.adjustBalance(amount, principal.getName().hashCode(), assetCode));
        resp.assertOk();
    }
}
