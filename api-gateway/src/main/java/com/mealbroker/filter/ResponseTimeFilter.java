package com.mealbroker.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Global filter that adds response time headers and basic logging
 */
@Component
public class ResponseTimeFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(ResponseTimeFilter.class);
    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Record start time
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());

        ServerHttpRequest request = exchange.getRequest();
        logger.info("Request: {} {}", request.getMethod(), request.getURI());

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // Add response time header
                    exchange.getResponse().getHeaders().add(
                            "X-Response-Time",
                            LocalDateTime.now().toString());

                    // Log response time
                    Long startTime = exchange.getAttribute(START_TIME);
                    if (startTime != null) {
                        long duration = System.currentTimeMillis() - startTime;
                        logger.info("Response: {} completed in {} ms",
                                exchange.getRequest().getURI(), duration);
                    }
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}