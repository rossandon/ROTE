package roteWeb.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roteShared.service.TradingEngineKafkaRequestResponseClient;
import roteShared.service.TradingEngineServiceRequest;

import java.security.Principal;
import java.util.HashMap;

@RestController
public class BalanceController {

    @Autowired
    TradingEngineKafkaRequestResponseClient client;

    @Autowired
    Principal principal;

    @GetMapping("balances/list")
    HashMap<String, Long> listBalances() throws Exception {
        var resp = client.send(TradingEngineServiceRequest.getBalances(Long.parseLong(principal.getName())));
        return resp.getBalancesResult().balances();
    }
}
