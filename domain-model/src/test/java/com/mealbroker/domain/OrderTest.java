package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private Customer customer;
    private Restaurant restaurant;
    private Branch branch;

    @BeforeEach
    void setUp() {
        customer = new Customer("John Doe", "john@example.com", "+12345678901");
        restaurant = new Restaurant("McDonald's", "Fast Food");
        branch = new Branch("Downtown", new Location(40.7128, -74.0060));
        branch.setRestaurant(restaurant);
        restaurant.addBranch(branch);
    }

    @Test
    void testConstructors() {
        // Default constructor
        Order order1 = new Order();
        assertNull(order1.getOrderId());
        assertNull(order1.getCustomer());
        assertNull(order1.getRestaurant());
        assertNull(order1.getBranch());
        assertNotNull(order1.getOrderTime());
        assertEquals(OrderStatus.NEW, order1.getStatus());
        assertTrue(order1.getItems().isEmpty());

        // Constructor with customer and restaurant
        Order order2 = new Order(customer, restaurant);
        assertNull(order2.getOrderId());
        assertEquals(customer, order2.getCustomer());
        assertEquals(restaurant, order2.getRestaurant());
        assertNull(order2.getBranch());
        assertNotNull(order2.getOrderTime());
        assertEquals(OrderStatus.NEW, order2.getStatus());
        assertTrue(order2.getItems().isEmpty());
    }

    @Test
    void testSetters() {
        Order order = new Order();

        Date orderTime = new Date();
        Location customerLocation = new Location(40.7128, -74.0060);

        order.setOrderId(1L);
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setBranch(branch);
        order.setOrderTime(orderTime);
        order.setStatus(OrderStatus.PROCESSING);
        order.setCustomerLocation(customerLocation);

        assertEquals(1L, order.getOrderId());
        assertEquals(customer, order.getCustomer());
        assertEquals(restaurant, order.getRestaurant());
        assertEquals(branch, order.getBranch());
        assertEquals(orderTime, order.getOrderTime());
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        assertEquals(customerLocation, order.getCustomerLocation());

        // Test getters for IDs
        assertEquals(customer.getCustomerId(), order.getCustomer().getCustomerId());
        assertEquals(restaurant.getRestaurantId(), order.getRestaurantId());
        assertEquals(branch.getBranchId(), order.getBranchId());
    }

    @Test
    void testOrderStatusTransitions() {
        Order order = new Order();

        // Initial state
        assertEquals(OrderStatus.NEW, order.getStatus());

        // Valid transitions
        order.setStatus(OrderStatus.PROCESSING);
        assertEquals(OrderStatus.PROCESSING, order.getStatus());

        order.setStatus(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        order.setStatus(OrderStatus.IN_PREPARATION);
        assertEquals(OrderStatus.IN_PREPARATION, order.getStatus());

        order.setStatus(OrderStatus.READY);
        assertEquals(OrderStatus.READY, order.getStatus());

        order.setStatus(OrderStatus.COMPLETED);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());

        // Invalid transition from COMPLETED
        assertThrows(IllegalStateException.class, () -> {
            order.setStatus(OrderStatus.PROCESSING);
        });
    }

    @Test
    void testCancelOrder() {
        Order order = new Order();

        // Cancel from NEW
        order.setStatus(OrderStatus.CANCELLED);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        // Cannot transition from CANCELLED
        Order order2 = new Order();
        order2.setStatus(OrderStatus.CANCELLED);

        assertThrows(IllegalStateException.class, () -> {
            order2.setStatus(OrderStatus.PROCESSING);
        });
    }

    @Test
    void testOrderItems() {
        Order order = new Order(customer, restaurant);

        // Test initial state
        assertTrue(order.getItems().isEmpty());

        // Add items
        OrderItem item1 = new OrderItem(1L, 1L, "Big Mac", 1, 5.99);
        OrderItem item2 = new OrderItem(2L, 2L, "Fries", 2, 2.99);

        order.addItem(item1);
        order.addItem(item2);

        assertEquals(2, order.getItems().size());
        assertTrue(order.getItems().contains(item1));
        assertTrue(order.getItems().contains(item2));
        assertEquals(order, item1.getOrder());
        assertEquals(order, item2.getOrder());

        // Get item by ID
        assertEquals(item1, order.getItemById(1L));
        assertEquals(item2, order.getItemById(2L));
        assertNull(order.getItemById(999L));

        // Remove item
        assertTrue(order.removeItem(item1));
        assertEquals(1, order.getItems().size());
        assertFalse(order.getItems().contains(item1));
        assertNull(item1.getOrder());

        // Remove by ID
        assertTrue(order.removeItemById(2L));
        assertTrue(order.getItems().isEmpty());

        // Remove non-existent item
        assertFalse(order.removeItemById(999L));
    }

    @Test
    void testCalculateTotal() {
        Order order = new Order(customer, restaurant);

        // Empty order
        assertEquals(0.0, order.calculateTotal());

        // Add items
        OrderItem item1 = new OrderItem(1L, "Big Mac", 1, 5.99);
        OrderItem item2 = new OrderItem(2L, "Fries", 2, 2.99);

        order.addItem(item1);
        order.addItem(item2);

        // 5.99 + (2.99 * 2) = 5.99 + 5.98 = 11.97
        double expectedTotal = 11.97;
        double calculatedTotal = order.calculateTotal();

        // Use delta for floating point comparison
        assertEquals(expectedTotal, calculatedTotal, 0.01);
    }

    @Test
    void testToString() {
        Order order = new Order(customer, restaurant);
        order.setOrderId(1L);

        String toString = order.toString();

        assertTrue(toString.contains("orderId=1"));
        assertTrue(toString.contains("status=NEW"));
    }

    @Test
    void testValidation() {
        // Empty order is not valid - requires customer and restaurant
        Order emptyOrder = new Order();
        assertFalse(validator.validate(emptyOrder).isEmpty());

        // Valid order
        Order validOrder = new Order(customer, restaurant);
        assertEquals(0, validator.validate(validOrder).size());
    }
}