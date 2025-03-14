package com.mealbroker.customer.config;

import com.mealbroker.customer.repository.CustomerRepository;
import com.mealbroker.domain.Customer;
import com.mealbroker.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class to seed customer data
 */
@Configuration
public class CustomerDataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDataSeeder.class);

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerDataSeeder(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Seeds the database with initial customer data
     * Only runs in development and test profiles
     */
    @Bean
    @Profile({"dev", "test"})
    public CommandLineRunner seedCustomerData() {
        return args -> {
            logger.info("Starting customer data seeding...");

            // Check if data already exists
            if (customerRepository.count() > 0) {
                logger.info("Customer database already seeded, skipping...");
                return;
            }

            List<Customer> customers = new ArrayList<>();

            // Create customers with locations in major Canadian cities
            Customer customer1 = new Customer("John Smith", "john.smith@example.com", "+16475551234");
            customer1.setLocation(new Location(43.6532, -79.3832)); // Toronto
            customers.add(customer1);

            Customer customer2 = new Customer("Jane Doe", "jane.doe@example.com", "+16045552345");
            customer2.setLocation(new Location(49.2827, -123.1207)); // Vancouver
            customers.add(customer2);

            Customer customer3 = new Customer("Robert Johnson", "robert.johnson@example.com", "+15145553456");
            customer3.setLocation(new Location(45.5017, -73.5673)); // Montreal
            customers.add(customer3);

            Customer customer4 = new Customer("Emily Wilson", "emily.wilson@example.com", "+14035554567");
            customer4.setLocation(new Location(51.0447, -114.0719)); // Calgary
            customers.add(customer4);

            Customer customer5 = new Customer("Michael Brown", "michael.brown@example.com", "+16137755678");
            customer5.setLocation(new Location(45.4215, -75.6972)); // Ottawa
            customers.add(customer5);

            Customer customer6 = new Customer("Sarah Davis", "sarah.davis@example.com", "+17805556789");
            customer6.setLocation(new Location(53.5461, -113.4938)); // Edmonton
            customers.add(customer6);

            Customer customer7 = new Customer("David Miller", "david.miller@example.com", "+14185557890");
            customer7.setLocation(new Location(46.8139, -71.2080)); // Quebec City
            customers.add(customer7);

            Customer customer8 = new Customer("Jennifer Taylor", "jennifer.taylor@example.com", "+12045558901");
            customer8.setLocation(new Location(49.8951, -97.1384)); // Winnipeg
            customers.add(customer8);

            // Save all customers
            customerRepository.saveAll(customers);

            logger.info("Customer data seeding completed. Added {} customers.", customers.size());
        };
    }
}