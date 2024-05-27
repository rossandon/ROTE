package webService.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class HttpSecurityConfig {

    @Autowired
    RoteAuthenticationManager roteAuthenticationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http
            .authorizeHttpRequests(r -> {
                r.requestMatchers(new AntPathRequestMatcher("/login/oauth2/code/google/**"),
                                new AntPathRequestMatcher("/system/ping"),
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/assets/**"))
                        .permitAll();
                r.anyRequest()
                        .authenticated();
            })
            .httpBasic(Customizer.withDefaults())
            .oauth2Login(Customizer.withDefaults())
            .authenticationManager(roteAuthenticationManager);

        return http.build();
    }
}
