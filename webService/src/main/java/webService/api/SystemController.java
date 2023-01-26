package webService.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
class SystemController {
    @GetMapping("system/ping")
    String ping() {
        return "pong";
    }

    @GetMapping("system/whoami")
    String whoami(Principal principal) {
        return principal.getName();
    }
}
