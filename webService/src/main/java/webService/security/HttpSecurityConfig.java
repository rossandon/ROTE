package webService.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import webService.security.cookie.CookieOAuth2AuthorizationRequestRepository;
import webService.security.cookie.CookieSetSuccessHandler;

@Configuration
public class HttpSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .authorizeRequests()
                .antMatchers("/login/oauth2/code/google/**", "/system/ping")
                    .permitAll()
                .anyRequest()
                    .authenticated()
            .and()
                .oauth2Login()
                .successHandler(successHandler)
                    .authorizationEndpoint()
                        .authorizationRequestRepository(new CookieOAuth2AuthorizationRequestRepository());
        return http.build();
    }

    @Autowired
    private CookieSetSuccessHandler successHandler;
}
