package webService.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;

@Component
@ConfigurationProperties(prefix = "backdoor-auth")
public class RoteAuthenticationManager implements AuthenticationProvider {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (password == null) {
            return authentication;
        }
        var specifiedPassword = authentication.getCredentials().toString();
        if (Objects.equals(specifiedPassword, password)) {
            return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), null, new ArrayList<>());
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == UsernamePasswordAuthenticationToken.class;
    }
}
