package com.epam.capstone.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Configuration
@EnableWebSecurity
@Import({ WebAppConfig.class })
public class WebSecurityConfig {

    private final Environment environment;

    @Autowired
    public WebSecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthorizationManager<RequestAuthorizationContext> healthAuthManager) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").anonymous()
                        .requestMatchers(HttpMethod.POST, "/login").anonymous()
                        .requestMatchers("/api/v1/health/**", "/metrics").access(healthAuthManager)
                        .requestMatchers(HttpMethod.POST, "/logout").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                );

        return http.build();
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> healthAuthorizationManager() {
        String ipsProperty = environment.getProperty("health.allowedIps", "");

        List<String> allowedIps = Arrays.stream(ipsProperty.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        String ipExpressionPart = "";
        if (!allowedIps.isEmpty()) {
            String joinedIps = allowedIps.stream()
                    .map(ip -> "hasIpAddress('" + ip + "')")
                    .collect(Collectors.joining(" or "));
            ipExpressionPart = " or (" + joinedIps + ")";
        }

        String healthExpression = "hasRole('ADMIN')" + ipExpressionPart;

        return new WebExpressionAuthorizationManager(healthExpression);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
