package com.mealbroker.broker.client;

import com.mealbroker.domain.Location;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for the Customer Service
 */
@FeignClient(name = "customer-service")
public interface CustomerServiceClient {

    /**
     * Validate if a customer exists and is active
     *
     * @param customerId the customer ID
     * @return true if the customer is valid
     */
    @GetMapping("/api/customers/{customerId}/validate")
    boolean validateCustomer(@PathVariable Long customerId);

    /**
     * Get a customer's location
     *
     * @param customerId the customer ID
     * @return the customer's location
     */
    @GetMapping("/api/customers/{customerId}/location")
    Location getCustomerLocation(@PathVariable Long customerId);
}
