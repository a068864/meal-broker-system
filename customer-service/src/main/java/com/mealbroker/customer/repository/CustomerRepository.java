package com.mealbroker.customer.repository;

import com.mealbroker.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find a customer by email
     *
     * @param email the email to search for
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Check if a customer exists with the given email
     *
     * @param email the email to check
     * @return true if a customer with the email exists
     */
    boolean existsByEmail(String email);

    List<Customer> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
            String name, String email, String phoneNumber);
}