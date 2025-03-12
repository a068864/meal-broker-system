package com.mealbroker.customer.service;

import com.mealbroker.customer.dto.CustomerDTO;
import com.mealbroker.domain.Location;

import java.util.List;

/**
 * Service interface for customer operations
 */
public interface CustomerService {

    /**
     * Create a new customer
     *
     * @param customerDTO the customer data
     * @return the created customer
     */
    CustomerDTO createCustomer(CustomerDTO customerDTO);

    /**
     * Get a customer by ID
     *
     * @param customerId the customer ID
     * @return the customer if found
     */
    CustomerDTO getCustomer(Long customerId);

    /**
     * Get all customers
     *
     * @return list of all customers
     */
    List<CustomerDTO> getAllCustomers();

    /**
     * Update a customer
     *
     * @param customerId  the customer ID
     * @param customerDTO the updated customer data
     * @return the updated customer
     */
    CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO);

    /**
     * Delete a customer
     *
     * @param customerId the customer ID
     */
    void deleteCustomer(Long customerId);

    /**
     * Find a customer by email
     *
     * @param email the email to search for
     * @return the customer if found
     */
    CustomerDTO getCustomerByEmail(String email);

    /**
     * Validate if a customer exists and is active
     *
     * @param customerId the customer ID
     * @return true if the customer is valid
     */
    boolean validateCustomer(Long customerId);

    /**
     * Get a customer's location
     *
     * @param customerId the customer ID
     * @return the customer's location
     */
    Location getCustomerLocation(Long customerId);

    /**
     * Update a customer's location
     *
     * @param customerId the customer ID
     * @param location   the new location
     * @return the updated customer
     */
    CustomerDTO updateCustomerLocation(Long customerId, Location location);

    /**
     * Search for customers based on various criteria
     *
     * @param query Search term to match against name, email or phone number
     * @return list of customers matching the search criteria
     */
    List<CustomerDTO> searchCustomers(String query);
}