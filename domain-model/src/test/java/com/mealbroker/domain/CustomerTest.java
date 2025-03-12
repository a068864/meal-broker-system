package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testConstructors() {
        // Default constructor
        Customer customer1 = new Customer();
        assertNull(customer1.getCustomerId());
        assertNull(customer1.getName());
        assertNull(customer1.getEmail());

        // Constructor with name, email, phone
        Customer customer2 = new Customer("John Doe", "john@example.com", "+12345678901");
        assertNull(customer2.getCustomerId());
        assertEquals("John Doe", customer2.getName());
        assertEquals("john@example.com", customer2.getEmail());
        assertEquals("+12345678901", customer2.getPhoneNumber());

        // Constructor with ID, name, email, phone
        Customer customer3 = new Customer(1L, "Jane Doe", "jane@example.com", "+19876543210");
        assertEquals(1L, customer3.getCustomerId());
        assertEquals("Jane Doe", customer3.getName());
        assertEquals("jane@example.com", customer3.getEmail());
        assertEquals("+19876543210", customer3.getPhoneNumber());
    }

    @Test
    void testSetters() {
        Customer customer = new Customer();

        customer.setCustomerId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhoneNumber("+12345678901");
        customer.setLocation(new Location(40.7128, -74.0060));

        assertEquals(1L, customer.getCustomerId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("+12345678901", customer.getPhoneNumber());
        assertNotNull(customer.getLocation());
        assertEquals(40.7128, customer.getLocation().getLatitude());
        assertEquals(-74.0060, customer.getLocation().getLongitude());
    }

    @Test
    void testOrderOperations() {
        Customer customer = new Customer("John Doe", "john@example.com", "+12345678901");
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Cuisine");

        // Test initial state
        assertTrue(customer.getOrders().isEmpty());

        // Test adding orders
        Order order1 = new Order(customer, restaurant);
        Order order2 = new Order(customer, restaurant);

        customer.addOrder(order1);
        customer.addOrder(order2);

        assertEquals(2, customer.getOrders().size());
        assertTrue(customer.getOrders().contains(order1));
        assertTrue(customer.getOrders().contains(order2));
        assertEquals(customer, order1.getCustomer());
        assertEquals(customer, order2.getCustomer());

        customer.removeOrder(order1);

        assertEquals(1, customer.getOrders().size());
        assertTrue(customer.getOrders().contains(order2));
        assertNull(order1.getCustomer());
    }

    @Test
    void testToString() {
        Customer customer = new Customer(1L, "John Doe", "john@example.com", "+12345678901");
        String toString = customer.toString();

        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("john@example.com"));
        assertTrue(toString.contains("+12345678901"));
    }

    @Test
    void testValidation() {
        // Valid customer
        Customer validCustomer = new Customer("John Doe", "john@example.com", "+12345678901");
        assertEquals(0, validator.validate(validCustomer).size());

        // Missing name
        Customer missingName = new Customer("", "john@example.com", "+12345678901");
        assertFalse(validator.validate(missingName).isEmpty());

        // Invalid email
        Customer invalidEmail = new Customer("John Doe", "invalid-email", "+12345678901");
        assertFalse(validator.validate(invalidEmail).isEmpty());

        // Invalid phone number
        Customer invalidPhone = new Customer("John Doe", "john@example.com", "invalid-phone");
        assertFalse(validator.validate(invalidPhone).isEmpty());
    }
}