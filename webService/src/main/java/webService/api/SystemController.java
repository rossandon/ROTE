package webService.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webService.api.models.WhoAmIResponse;

import java.security.Principal;

@RestController
class SystemController {
    @GetMapping("system/ping")
    String ping() {
        return "pong";
    }

    @GetMapping("system/whoami")
    WhoAmIResponse whoami(Principal principal) {
        return new WhoAmIResponse(principal.getName());
    }
}
