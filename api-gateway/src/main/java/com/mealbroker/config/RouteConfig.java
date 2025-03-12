package com.mealbroker.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Route configuration for the API Gateway
 */
@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Customer Service Routes
                .route("customer-service", r -> r.path("/api/customers/**")
                        .uri("lb://customer-service"))

                // Restaurant Service Routes
                .route("restaurant-service", r -> r.path("/api/restaurants/**")
                        .uri("lb://restaurant-service"))

                // Order Service Routes
                .route("order-service", r -> r.path("/api/orders/**")
                        .uri("lb://order-service"))

                // Order Broker Routes
                .route("order-broker", r -> r.path("/api/broker/**")
                        .uri("lb://order-broker"))

                // Location Service Routes
                .route("location-service", r -> r.path("/api/locations/**")
                        .uri("lb://location-service"))

                .build();
    }
}