package com.mealbroker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple authentication controller
 */
@RestController
@RequestMapping("/api/auth")
public class SimpleAuthController {

    /**
     * Get user info for the authenticated user
     */
    @GetMapping("/me")
    public Mono<Map<String, Object>> getUserInfo(Mono<Principal> principal) {
        return principal
                .map(p -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("username", p.getName());
                    userInfo.put("isAuthenticated", true);
                    return userInfo;
                });
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Auth service is up and running!");
    }
}