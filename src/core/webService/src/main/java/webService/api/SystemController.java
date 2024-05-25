package webService.api;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
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
        var oauthPrincipal = (OAuth2AuthenticationToken)principal;
        var email = oauthPrincipal.getPrincipal().getAttribute("email");
        return new WhoAmIResponse((String)email);
    }
}
