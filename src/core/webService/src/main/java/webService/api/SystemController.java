package webService.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webService.api.models.WhoAmIResponse;
import webService.security.RoteUserContext;

@RestController
class SystemController {
    @GetMapping("system/ping")
    String ping() {
        return "pong";
    }

    @GetMapping("system/whoami")
    WhoAmIResponse whoami() throws Exception {
        return new WhoAmIResponse(RoteUserContext.GetDisplayName(), RoteUserContext.GetAccountId());
    }
}
