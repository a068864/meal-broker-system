package com.mealbroker.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Customer Service Application
 * Manages customer data for the Meal Broker System
 */
@SpringBootApplication
@EnableDiscoveryClient
@EntityScan(basePackages = {"com.mealbroker.domain"})
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}