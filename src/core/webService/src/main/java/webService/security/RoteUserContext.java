package webService.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class RoteUserContext {
    public long getAccountId() throws Exception {
        var principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal instanceof OAuth2AuthenticationToken oauthPrincipal) {
            return Long.parseLong(oauthPrincipal.getPrincipal().getName().substring(3));
        }
        if (principal instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return usernamePasswordAuthenticationToken.getName().hashCode();
        }
        throw new Exception();
    }

    public String getDisplayName() throws Exception {
        var principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal instanceof OAuth2AuthenticationToken oauthPrincipal) {
            return oauthPrincipal.getPrincipal().getAttribute("email");
        }
        if (principal instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return usernamePasswordAuthenticationToken.getName();
        }
        throw new Exception();
    }
}
