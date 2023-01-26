package webService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shared.service.TradingEngineKafkaRequestResponseClient;
import shared.service.TradingEngineServiceRequest;

import java.security.Principal;
import java.util.HashMap;

@RestController
public class BalanceController {

    @Autowired
    TradingEngineKafkaRequestResponseClient client;

    @GetMapping("balances/list")
    HashMap<String, Long> listBalances(Principal principal) throws Exception {
        var resp = client.send(TradingEngineServiceRequest.getBalances(principal.getName().hashCode()));
        return resp.getBalancesResult().balances();
    }

    @GetMapping("balances/add")
    void addBalances(Principal principal) throws Exception {
        client.send(TradingEngineServiceRequest.adjustBalance(100, principal.getName().hashCode(), "USD"));
    }
}
