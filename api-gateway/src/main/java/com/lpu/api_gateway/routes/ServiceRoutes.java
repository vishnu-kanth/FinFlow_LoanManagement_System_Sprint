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

                //Auth Service
                .route("auth-service", r -> r
                        .path("/gateway/auth/**")
                        .filters(f -> f
                                .rewritePath("/gateway/(?<segment>.*)", "/${segment}")
                                .filter(jwtAuthFilter)
                        )
                        .uri("lb://AUTH-SERVICE")
                )

                // Application Service
                .route("application-service", r -> r
                        .path("/gateway/applications/**")
                        .filters(f -> f
                                .rewritePath("/gateway/(?<segment>.*)", "/${segment}")
                                .filter(jwtAuthFilter)
                        )
                        .uri("lb://APPLICATION-SERVICE")
                )

                // Document Service
                .route("document-service", r -> r
                        .path("/gateway/documents/**")
                        .filters(f -> f
                                .rewritePath("/gateway/(?<segment>.*)", "/${segment}")
                                .filter(jwtAuthFilter)
                        )
                        .uri("lb://DOCUMENT-SERVICE")
                )

                // Admin Service
                .route("admin-service", r -> r
                        .path("/gateway/admin/**")
                        .filters(f -> f
                                .rewritePath("/gateway/(?<segment>.*)", "/${segment}")
                                .filter(jwtAuthFilter)
                        )
                        .uri("lb://ADMIN-SERVICE")
                )

                .build();
    }
}
