package com.mealbroker.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Restaurant Service Application
 * Manages restaurant and branch data for the Meal Broker System
 */
@SpringBootApplication
@EnableDiscoveryClient
@EntityScan(basePackages = {"com.mealbroker.domain"})
public class RestaurantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}