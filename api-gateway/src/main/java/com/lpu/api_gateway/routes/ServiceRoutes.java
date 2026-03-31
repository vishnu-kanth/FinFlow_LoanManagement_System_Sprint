package com.lpu.api_gateway.routes;

import com.lpu.api_gateway.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceRoutes {

    private static final String AUTH_SERVICE = "lb://AUTH-SERVICE";
    private static final String APPLICATION_SERVICE = "lb://APPLICATION-SERVICE";
    private static final String DOCUMENT_SERVICE = "lb://DOCUMENT-SERVICE";
    private static final String ADMIN_SERVICE = "lb://ADMIN-SERVICE";

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
                        .filters(f -> f.stripPrefix(1))
                        .uri(AUTH_SERVICE)
                )

                .route("application-service-docs", r -> r
                        .path("/gateway/applications/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(APPLICATION_SERVICE)
                )

                .route("document-service-docs", r -> r
                        .path("/gateway/documents/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(DOCUMENT_SERVICE)
                )

                .route("admin-service-docs", r -> r
                        .path("/gateway/admin/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(ADMIN_SERVICE)
                )

                // Auth Service
                .route("auth-service", r -> r
                        .path("/gateway/auth/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthFilter)
                        )
                        .uri(AUTH_SERVICE)
                )

                // Application Service
                .route("application-service", r -> r
                        .path("/gateway/applications/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthFilter)
                        )
                        .uri(APPLICATION_SERVICE)
                )

                // Document Service
                .route("document-service", r -> r
                        .path("/gateway/documents/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthFilter)
                        )
                        .uri(DOCUMENT_SERVICE)
                )

                // Admin Service
                .route("admin-service", r -> r
                        .path("/gateway/admin/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthFilter)
                        )
                        .uri(ADMIN_SERVICE)
                )

                .build();
    }
}
