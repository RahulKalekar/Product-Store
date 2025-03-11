package com.rahul.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final String[] freeResourceUrls = {"/swagger-ui.html/**", "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-resources/**","/webjars/**", "/api-docs/**", "/aggregate/**", "/actuator/prometheus" };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http

                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(freeResourceUrls).permitAll()  // Change to permitAll()
                        .pathMatchers("/api/orders/create").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .pathMatchers("/api/products/all", "/products/{id}").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .pathMatchers("/**").hasAuthority("ROLE_ADMIN")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    private ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess == null) {
                return Collections.emptyList();
            }

            Map<String, Object> gatewayService = (Map<String, Object>) resourceAccess.get("gateway-service");
            if (gatewayService == null) {
                return Collections.emptyList();
            }

            Collection<String> roles = (Collection<String>) gatewayService.get("roles");
            if (roles == null) {
                return Collections.emptyList();
            }

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Convert to GrantedAuthority
                    .collect(Collectors.toList());
        });

        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}
