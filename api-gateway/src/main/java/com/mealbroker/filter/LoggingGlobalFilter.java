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

import java.util.UUID;

/**
 * Global filter that applies to all routes
 * Adds request ID and logging
 */
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGlobalFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Add request time to exchange attributes
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());

        // Get or generate request ID
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        // Add request ID to request headers
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();

        // Log the request
        logger.info("Request: {} {} from {}, Request ID: {}",
                request.getMethod(), request.getURI(), request.getRemoteAddress(), requestId);

        // Continue the filter chain with modified request
        String finalRequestId = requestId;
        return chain.filter(exchange.mutate().request(request).build())
                .then(Mono.fromRunnable(() -> {
                    // Log response after the request is complete
                    Long startTime = exchange.getAttribute(START_TIME);
                    if (startTime != null) {
                        long duration = System.currentTimeMillis() - startTime;
                        logger.info("Response: {} for {} completed in {} ms, Request ID: {}",
                                exchange.getResponse().getStatusCode(), request.getURI(), duration, finalRequestId);
                    }
                }));
    }

    @Override
    public int getOrder() {
        // Set to run first in the filter chain
        return Ordered.HIGHEST_PRECEDENCE;
    }
}