package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private Customer testCustomer;
    private Location torontoLocation;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John Doe", "john@example.com", "+12345678901");
        torontoLocation = new Location(43.6532, -79.3832);
    }

    @Test
    void testConstructors() {
        Customer customer1 = new Customer();
        assertNull(customer1.getCustomerId());
        assertNull(customer1.getName());
        assertNull(customer1.getEmail());
        assertNull(customer1.getPhoneNumber());
        assertNull(customer1.getLocation());
        assertNotNull(customer1.getOrders());
        assertTrue(customer1.getOrders().isEmpty());

        Customer customer2 = new Customer("John Doe", "john@example.com", "+12345678901");
        assertNull(customer2.getCustomerId());
        assertEquals("John Doe", customer2.getName());
        assertEquals("john@example.com", customer2.getEmail());
        assertEquals("+12345678901", customer2.getPhoneNumber());
        assertNull(customer2.getLocation());
        assertNotNull(customer2.getOrders());
        assertTrue(customer2.getOrders().isEmpty());

        Customer customer3 = new Customer(1L, "Jane Doe", "jane@example.com", "+19876543210");
        assertEquals(1L, customer3.getCustomerId());
        assertEquals("Jane Doe", customer3.getName());
        assertEquals("jane@example.com", customer3.getEmail());
        assertEquals("+19876543210", customer3.getPhoneNumber());
        assertNull(customer3.getLocation());
        assertNotNull(customer3.getOrders());
        assertTrue(customer3.getOrders().isEmpty());
    }

    @Test
    void testSetters() {
        Customer customer = new Customer();

        customer.setCustomerId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhoneNumber("+12345678901");
        customer.setLocation(torontoLocation);

        assertEquals(1L, customer.getCustomerId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("+12345678901", customer.getPhoneNumber());
        assertNotNull(customer.getLocation());
        assertEquals(43.6532, customer.getLocation().getLatitude());
        assertEquals(-79.3832, customer.getLocation().getLongitude());
    }

    @Test
    void testOrderOperations() {
        Customer customer = testCustomer;
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Cuisine");

        // Test initial state
        assertTrue(customer.getOrders().isEmpty());

        // Test adding orders
        Order order1 = new Order(customer, restaurant);
        Order order2 = new Order(customer, restaurant);

        // Verify bidirectional relationship is maintained
        assertEquals(customer, order1.getCustomer());
        assertEquals(customer, order2.getCustomer());
        assertEquals(2, customer.getOrders().size());
        assertTrue(customer.getOrders().contains(order1));
        assertTrue(customer.getOrders().contains(order2));

        // Test removing an order
        customer.removeOrder(order1);
        assertEquals(1, customer.getOrders().size());
        assertTrue(customer.getOrders().contains(order2));
        assertFalse(customer.getOrders().contains(order1));
        assertNull(order1.getCustomer());
        assertEquals(customer, order2.getCustomer());

        // Test attempting to remove the same order again (should have no effect)
        customer.removeOrder(order1);
        assertEquals(1, customer.getOrders().size());

        // Test removing a null order (should have no effect)
        customer.removeOrder(null);
        assertEquals(1, customer.getOrders().size());

        // Test setOrders method
        List<Order> newOrders = Collections.singletonList(order1);
        customer.setOrders(newOrders);
        assertEquals(1, customer.getOrders().size());
        assertTrue(customer.getOrders().contains(order1));
    }

    @Test
    void testToString() {
        Customer customer = new Customer(1L, "John Doe", "john@example.com", "+12345678901");
        String toString = customer.toString();

        assertTrue(toString.contains("customerId=1"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("john@example.com"));
        assertTrue(toString.contains("+12345678901"));
    }

    @Test
    void testValidCustomer() {
        // Valid customer
        Customer validCustomer = new Customer("John Doe", "john@example.com", "+12345678901");
        assertEquals(0, validator.validate(validCustomer).size());
    }

    @Test
    void testInvalidNameValidation() {
        // Missing name
        Customer missingName = new Customer("", "john@example.com", "+12345678901");
        assertFalse(validator.validate(missingName).isEmpty());
    }

    @Test
    void testInvalidEmailValidation() {
        // Invalid email format
        Customer invalidEmail = new Customer("John Doe", "invalid-email", "+12345678901");
        assertFalse(validator.validate(invalidEmail).isEmpty());

        // Null email
        Customer nullEmail = new Customer("John Doe", null, "+12345678901");
        assertFalse(validator.validate(nullEmail).isEmpty());
    }

    @Test
    void testInvalidPhoneValidation() {
        // Invalid phone number (missing digits)
        Customer invalidPhone1 = new Customer("John Doe", "john@example.com", "+123");
        assertFalse(validator.validate(invalidPhone1).isEmpty());

        // Invalid phone number (contains letters)
        Customer invalidPhone2 = new Customer("John Doe", "john@example.com", "+123456789abc");
        assertFalse(validator.validate(invalidPhone2).isEmpty());
    }

    @Test
    void testLocationRelationship() {
        Customer customer = testCustomer;

        // Initially null
        assertNull(customer.getLocation());

        // Set location
        customer.setLocation(torontoLocation);
        assertEquals(torontoLocation, customer.getLocation());
        assertEquals(43.6532, customer.getLocation().getLatitude());
        assertEquals(-79.3832, customer.getLocation().getLongitude());

        // Update location
        Location montreal = new Location(45.5017, -73.5673);
        customer.setLocation(montreal);
        assertEquals(montreal, customer.getLocation());
        assertEquals(45.5017, customer.getLocation().getLatitude());
        assertEquals(-73.5673, customer.getLocation().getLongitude());

        // Set to null again
        customer.setLocation(null);
        assertNull(customer.getLocation());
    }

    @Test
    void testDuplicateOrders() {
        Customer customer = testCustomer;
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Cuisine");

        // Create an order
        Order order = new Order(customer, restaurant);

        // At this point, the order should already be added to the customer due to bidirectional relationship
        assertEquals(1, customer.getOrders().size());

        // Try to add the same order again (should have no effect)
        customer.addOrder(order);
        assertEquals(1, customer.getOrders().size(), "Duplicate orders should not be added");

        // Create a new order with the same properties
        Order similarOrder = new Order(customer, restaurant);

        // Similarly, this order should already be added to the customer
        assertEquals(2, customer.getOrders().size());

        // Add null order (should have no effect)
        customer.addOrder(null);
        assertEquals(2, customer.getOrders().size());
    }

    @Test
    void testOrderRemovalFromCustomer() {
        Customer customer = testCustomer;
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Cuisine");
        Order order = new Order(customer, restaurant);

        // Test removing from order side
        order.setCustomer(null);
        assertNull(order.getCustomer());
        assertFalse(customer.getOrders().contains(order));

        // Re-add the order
        order.setCustomer(customer);
        assertTrue(customer.getOrders().contains(order));

        // Test removing from customer side
        customer.removeOrder(order);
        assertFalse(customer.getOrders().contains(order));
        assertNull(order.getCustomer());
    }
}