package com.mealbroker.customer.service.impl;

import com.mealbroker.customer.dto.CustomerDTO;
import com.mealbroker.customer.exception.CustomerNotFoundException;
import com.mealbroker.customer.exception.EmailAlreadyExistsException;
import com.mealbroker.customer.repository.CustomerRepository;
import com.mealbroker.customer.service.CustomerService;
import com.mealbroker.domain.Customer;
import com.mealbroker.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the CustomerService interface
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        // Check if email already exists
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + customerDTO.getEmail());
        }

        // Convert DTO to entity
        Customer customer = new Customer(
                customerDTO.getName(),
                customerDTO.getEmail(),
                customerDTO.getPhoneNumber()
        );

        // Set location if provided
        if (customerDTO.getLocation() != null) {
            customer.setLocation(customerDTO.getLocation());
        }

        // Save the customer
        Customer savedCustomer = customerRepository.save(customer);

        // Convert back to DTO and return
        return convertToDTO(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));

        return convertToDTO(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));

        // Check if email changed and if it's already in use
        if (!customer.getEmail().equals(customerDTO.getEmail())
                && customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + customerDTO.getEmail());
        }

        // Update customer fields
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());

        if (customerDTO.getLocation() != null) {
            customer.setLocation(customerDTO.getLocation());
        }

        // Save updated customer
        Customer updatedCustomer = customerRepository.save(customer);

        return convertToDTO(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }

        customerRepository.deleteById(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));

        return convertToDTO(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateCustomer(Long customerId) {
        return customerRepository.existsById(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Location getCustomerLocation(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));

        if (customer.getLocation() == null) {
            throw new IllegalStateException("Location not set for customer with ID: " + customerId);
        }

        return customer.getLocation();
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomerLocation(Long customerId, Location location) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));

        customer.setLocation(location);
        Customer updatedCustomer = customerRepository.save(customer);

        return convertToDTO(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchTerm = "%" + query.toLowerCase() + "%";
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm);

        return customers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to convert Customer entity to CustomerDTO
     */
    private CustomerDTO convertToDTO(Customer customer) {
        return new CustomerDTO(
                customer.getCustomerId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getLocation()
        );
    }

}