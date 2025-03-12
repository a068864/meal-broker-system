package com.mealbroker.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Order Service Application
 * Manages orders in the Meal Broker System
 */
@SpringBootApplication
@EnableDiscoveryClient
@EntityScan(basePackages = {"com.mealbroker.domain"})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}