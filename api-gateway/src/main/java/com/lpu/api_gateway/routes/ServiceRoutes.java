package com.lpu.api_gateway.routes;

import com.lpu.api_gateway.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceRoutes {

    private final JwtAuthFilter jwtAuthFilter;

    public ServiceRoutes(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public RouteLocator serviceRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()

                // Swagger Docs
                .route("auth-service-docs", r -> r
                        .path("/gateway/auth/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/gateway/auth/(?<segment>.*)", "/auth/${segment}")
                        )
                        .uri("lb://AUTH-SERVICE")
                )

                .route("application-service-docs", r -> r
                        .path("/gateway/applications/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/gateway/applications/(?<segment>.*)", "/applications/${segment}")
                        )
                        .uri("lb://APPLICATION-SERVICE")
                )

                .route("document-service-docs", r -> r
                        .path("/gateway/documents/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/gateway/documents/(?<segment>.*)", "/documents/${segment}")
                        )
                        .uri("lb://DOCUMENT-SERVICE")
                )

                .route("admin-service-docs", r -> r
                        .path("/gateway/admin/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/gateway/admin/(?<segment>.*)", "/admin/${segment}")
                        )
                        .uri("lb://ADMIN-SERVICE")
                )

                // Auth Service
                .route("auth-service", r -> r
                        .path("/gateway/auth/**")
                        .filters(f -> f
                                .rewritePath("/gateway/auth/(?<segment>.*)", "/auth/${segment}")
                                .filter(jwtAuthFilter)
                        )
                        .uri("lb://AUTH-SERVICE")
                )

                // Application Service
                .route("application-service", r -> r
                        .path("/gateway/applications/**")
                        .filters(f -> f
                                .rewritePath("/gateway/applications/(?<segment>.*)", "/applications/${segment}")
                                .filter(jwtAuthFilter)
                        )
                        .uri("lb://APPLICATION-SERVICE")
                )

                // Document Service
                .route("document-service", r -> r
                        .path("/gateway/documents/**")
                        .filters(f -> f
                                .rewritePath("/gateway/documents/(?<segment>.*)", "/documents/${segment}")
                                .filter(jwtAuthFilter)
                        )
                        .uri("lb://DOCUMENT-SERVICE")
                )

                // Admin Service
                .route("admin-service", r -> r
                        .path("/gateway/admin/**")
                        .filters(f -> f
                                .rewritePath("/gateway/admin/(?<segment>.*)", "/admin/${segment}")
                                .filter(jwtAuthFilter)
                        )
                        .uri("lb://ADMIN-SERVICE")
                )

                .build();
    }
}
