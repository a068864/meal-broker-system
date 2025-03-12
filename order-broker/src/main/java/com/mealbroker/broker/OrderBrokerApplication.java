package com.mealbroker.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Order Broker Application
 * Implements the Broker pattern for the Meal Broker System
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EntityScan(basePackages = {"com.mealbroker.domain"})
public class OrderBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderBrokerApplication.class, args);
    }
}