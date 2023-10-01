package webService.security.cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import webService.security.jwt.JwtHelper;
import webService.utils.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CookieSetSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    public JwtHelper jwtHelper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        var token = (OAuth2AuthenticationToken) authentication;
        var email = token.getPrincipal().getAttribute("email").toString();
        //var authorities = token.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        Map<String, String> claims = new HashMap<>();
        var jwt = jwtHelper.createJwtForClaims(email, claims);

        CookieUtils.addCookie(response, CookieConsts.ROTEAuthCookieName, jwt, 100000);
        response.sendRedirect("/system/whoami");
    }
}
