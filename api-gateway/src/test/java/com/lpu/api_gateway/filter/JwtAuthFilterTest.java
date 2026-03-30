package com.lpu.api_gateway.filter;

import com.lpu.api_gateway.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void filterAllowsPublicSwaggerPathWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/gateway/auth/v3/api-docs").build()
        );
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        jwtAuthFilter.filter(exchange, filterChain).block();

        verify(filterChain).filter(exchange);
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void filterAllowsCorsPreflightWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.method(HttpMethod.OPTIONS, "/gateway/applications/1")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .build()
        );
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        jwtAuthFilter.filter(exchange, filterChain).block();

        verify(filterChain).filter(exchange);
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void filterAddsForwardedHeadersForValidToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/gateway/applications/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .build()
        );
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid-token")).thenReturn("user@finflow.com");
        when(jwtUtil.extractRole("valid-token")).thenReturn("ROLE_APPLICANT");
        when(jwtUtil.extractUserId("valid-token")).thenReturn(42L);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        jwtAuthFilter.filter(exchange, filterChain).block();

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain).filter(captor.capture());

        ServerWebExchange forwardedExchange = captor.getValue();
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Email"))
                .isEqualTo("user@finflow.com");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Role"))
                .isEqualTo("ROLE_APPLICANT");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Id"))
                .isEqualTo("42");
    }

    @Test
    void filterRejectsForbiddenRole() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/gateway/admin/stats")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .build()
        );
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid-token")).thenReturn("user@finflow.com");
        when(jwtUtil.extractRole("valid-token")).thenReturn("ROLE_APPLICANT");
        when(jwtUtil.extractUserId("valid-token")).thenReturn(42L);

        jwtAuthFilter.filter(exchange, filterChain).block();

        verify(filterChain, never()).filter(any(ServerWebExchange.class));
        assertThat(exchange.getResponse().getStatusCode()).hasToString("403 FORBIDDEN");
    }
}
