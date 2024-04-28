package webService.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webService.api.models.WhoAmIResponse;
import webService.security.cookie.CookieConsts;
import webService.utils.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @GetMapping("system/logout")
    void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CookieUtils.deleteCookie(request, response, CookieConsts.ROTEAuthCookieName);
        response.sendRedirect("/");
    }
}
