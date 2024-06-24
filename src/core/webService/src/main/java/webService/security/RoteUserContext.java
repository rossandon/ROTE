package webService.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;

public class RoteUserContext {
    private final Principal principal;

    public RoteUserContext(Principal principal) {
        this.principal = principal;
    }

    public static long GetAccountId() throws Exception {
        var principal = SecurityContextHolder.getContext().getAuthentication();
        return new RoteUserContext(principal).getAccountId();
    }

    public static String GetDisplayName() throws Exception {
        var principal = SecurityContextHolder.getContext().getAuthentication();
        return new RoteUserContext(principal).getDisplayName();
    }

    public long getAccountId() throws Exception {
        if (principal instanceof OAuth2AuthenticationToken oauthPrincipal) {
            return Long.parseLong(oauthPrincipal.getPrincipal().getName().substring(3));
        }
        if (principal instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return usernamePasswordAuthenticationToken.getName().hashCode();
        }
        throw new Exception();
    }

    public String getDisplayName() throws Exception {
        if (principal instanceof OAuth2AuthenticationToken oauthPrincipal) {
            return oauthPrincipal.getPrincipal().getAttribute("email");
        }
        if (principal instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return usernamePasswordAuthenticationToken.getName();
        }
        throw new Exception();
    }
}
