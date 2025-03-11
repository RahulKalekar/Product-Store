package com.rahul.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.security.oauth2.jwt.Jwt;

@Component
@Slf4j
public class UserInfoFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthenticationToken.class)
                .map(token -> {
                    Jwt jwt = token.getToken();

                    // Extract email and name from JWT claims
                    String email = jwt.getClaim("email");
                    String name = jwt.getClaim("name");

                    log.debug("Extracted email: {}, name: {}", email, name);

                    // Add these as headers to the request
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Email", email)
                            .header("X-User-Name", name)
                            .build();

                    // Return modified exchange
                    return exchange.mutate()
                            .request(modifiedRequest)
                            .build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }
}