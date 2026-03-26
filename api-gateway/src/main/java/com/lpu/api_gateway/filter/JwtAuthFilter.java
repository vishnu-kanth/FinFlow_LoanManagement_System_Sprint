package com.lpu.api_gateway.filter;

import com.lpu.api_gateway.security.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter implements GatewayFilter, Ordered {

    private final JwtUtil jwtUtil;

    // RBAC: map gateway path prefixes to the roles that are allowed
    private static final Map<String, List<String>> PATH_ROLE_MAP = Map.of(
            "/gateway/admin", List.of("ADMIN"),
            "/gateway/applications", List.of("APPLICANT", "ADMIN"),
            "/gateway/documents", List.of("APPLICANT", "ADMIN")
    );

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // ── 1. Extract and validate token ──────────────────────────────
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return unauthorized(exchange);
        }

        // ── 2. Extract claims ──────────────────────────────────────────
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        // ── 3. RBAC – check role against path ──────────────────────────
        for (Map.Entry<String, List<String>> entry : PATH_ROLE_MAP.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                if (!entry.getValue().contains(role)) {
                    return forbidden(exchange);
                }
                break;
            }
        }

        // ── 4. Forward user info to downstream services ────────────────
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.headers(headers -> {
                    headers.add("X-User-Email", username);
                    headers.add("X-User-Role", role);
                }))
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -1; // run before other filters
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }
}
