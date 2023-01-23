package roteWeb.api;

import org.springframework.security.oauth2.core.user.OAuth2User;
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
