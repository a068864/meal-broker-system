package com.mealbroker.broker.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

/**
 * Configuration for Resilience4j circuit breakers
 * Only contains a fallback configuration in case the YAML config fails to load
 */
@Configuration
public class Resilience4jConfig {

    /**
     * Configures default circuit breaker settings
     */
    @Bean
    @Primary
    public CircuitBreakerConfig defaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();
    }

    /**
     * Fallback time limiter settings if YAML configuration is not loaded
     */
    @Bean
    @Primary
    public TimeLimiterConfig defaultTimeLimiterConfig() {
        return TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4))
                .build();
    }
}