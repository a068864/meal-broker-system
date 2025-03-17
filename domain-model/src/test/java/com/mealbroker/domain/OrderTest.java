package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private Customer customer;
    private Restaurant restaurant;
    private Branch branch;
    private Location customerLocation;
    private Date testOrderTime;

    @BeforeEach
    void setUp() {
        customer = new Customer("John Doe", "john@example.com", "+12345678901");
        customer.setCustomerId(1L);

        restaurant = new Restaurant("McDonald's", "Fast Food");
        restaurant.setRestaurantId(2L);

        branch = new Branch("Downtown", new Location(40.7128, -74.0060));
        branch.setRestaurant(restaurant);
        branch.setBranchId(3L);
        restaurant.addBranch(branch);

        customerLocation = new Location(40.7135, -74.0070);
        testOrderTime = new Date(1640995200000L); // 2022-01-01
    }

    @Test
    void testConstructors() {
        Order order1 = new Order();
        assertNull(order1.getOrderId());
        assertNull(order1.getCustomer());
        assertNull(order1.getRestaurant());
        assertNull(order1.getBranch());
        assertNotNull(order1.getOrderTime());
        assertEquals(OrderStatus.NEW, order1.getStatus());
        assertTrue(order1.getItems().isEmpty());

        Order order2 = new Order(customer, restaurant);
        assertNull(order2.getOrderId());
        assertEquals(customer, order2.getCustomer());
        assertEquals(restaurant, order2.getRestaurant());
        assertNull(order2.getBranch());
        assertNotNull(order2.getOrderTime());
        assertEquals(OrderStatus.NEW, order2.getStatus());
        assertTrue(order2.getItems().isEmpty());

        assertTrue(customer.getOrders().contains(order2));
        assertTrue(restaurant.getOrders().contains(order2));
    }

    @Test
    void testSetters() {
        Order order = new Order();

        order.setOrderId(1L);
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setBranch(branch);
        order.setOrderTime(testOrderTime);
        order.setStatus(OrderStatus.PROCESSING);
        order.setCustomerLocation(customerLocation);

        assertEquals(1L, order.getOrderId());
        assertEquals(customer, order.getCustomer());
        assertEquals(restaurant, order.getRestaurant());
        assertEquals(branch, order.getBranch());
        assertEquals(testOrderTime, order.getOrderTime());
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        assertEquals(customerLocation, order.getCustomerLocation());

        // Test getters for IDs
        assertEquals(customer.getCustomerId(), order.getCustomer().getCustomerId());
        assertEquals(restaurant.getRestaurantId(), order.getRestaurantId());
        assertEquals(branch.getBranchId(), order.getBranchId());

        // Test bi-directional relationships
        assertTrue(customer.getOrders().contains(order));
        assertTrue(restaurant.getOrders().contains(order));
        assertTrue(branch.getOrders().contains(order));
    }

    @Test
    void testOrderStatusTransitions() {
        Order order = new Order();

        // Initial state
        assertEquals(OrderStatus.NEW, order.getStatus());

        // Test all valid transitions
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

        // Invalid transition from COMPLETED to PROCESSING
        Exception completedToProcessing = assertThrows(IllegalStateException.class, () -> {
            order.setStatus(OrderStatus.PROCESSING);
        });
        assertTrue(completedToProcessing.getMessage().contains("Invalid status transition"));

        // Invalid transition from COMPLETED to CANCELLED
        Exception completedToCancelled = assertThrows(IllegalStateException.class, () -> {
            order.setStatus(OrderStatus.CANCELLED);
        });
        assertTrue(completedToCancelled.getMessage().contains("Invalid status transition"));
    }

    @Test
    void testOrderStatusTransitionsToCancel() {
        // Test cancellation from different states
        OrderStatus[] validForCancellation = {
                OrderStatus.NEW,
                OrderStatus.PROCESSING,
                OrderStatus.CONFIRMED,
                OrderStatus.IN_PREPARATION,
                OrderStatus.READY
        };

        for (OrderStatus status : validForCancellation) {
            Order order = new Order();
            if (status != OrderStatus.NEW) {
                order.setStatus(status);
            }

            // Should be able to cancel from this state
            order.setStatus(OrderStatus.CANCELLED);
            assertEquals(OrderStatus.CANCELLED, order.getStatus());

            // Cannot transition from CANCELLED
            Exception ex = assertThrows(IllegalStateException.class, () -> {
                order.setStatus(OrderStatus.PROCESSING);
            });
            assertTrue(ex.getMessage().contains("Invalid status transition"));
        }
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

        // Add same item again (should have no effect)
        order.addItem(item1);
        assertEquals(2, order.getItems().size());

        // Add null item (should have no effect)
        order.addItem(null);
        assertEquals(2, order.getItems().size());

        // Get item by ID
        assertEquals(item1, order.getItemById(1L));
        assertEquals(item2, order.getItemById(2L));
        assertNull(order.getItemById(999L));

        // Remove item using object reference
        assertTrue(order.removeItem(item1));
        assertEquals(1, order.getItems().size());
        assertFalse(order.getItems().contains(item1));
        assertNull(item1.getOrder());

        // Try to remove same item again (should return false)
        assertFalse(order.removeItem(item1));
        assertEquals(1, order.getItems().size());

        // Remove by ID
        assertTrue(order.removeItemById(2L));
        assertTrue(order.getItems().isEmpty());

        // Try to remove by non-existent ID (should return false)
        assertFalse(order.removeItemById(999L));

        // Set items list
        List<OrderItem> newItems = Arrays.asList(item1, item2);
        order.setItems(newItems);
        assertEquals(2, order.getItems().size());

        // Ensure bidirectional relationship is maintained after setItems
        assertEquals(order, item1.getOrder());
        assertEquals(order, item2.getOrder());
    }

    @Test
    void testCalculateTotal() {
        Order order = new Order(customer, restaurant);

        // Empty order
        assertEquals(0.0, order.calculateTotal());

        // Add items
        OrderItem item1 = new OrderItem(101L, "Big Mac", 1, 5.99);
        OrderItem item2 = new OrderItem(102L, "Fries", 2, 2.99);
        OrderItem item3 = new OrderItem(103L, "Soda", 1, 1.49);

        order.addItem(item1);
        order.addItem(item2);
        order.addItem(item3);

        // Expected: 5.99 + (2.99 * 2) + 1.49 = 5.99 + 5.98 + 1.49 = 13.46
        double expectedTotal = 13.46;
        double calculatedTotal = order.calculateTotal();

        // Use delta for floating point comparison
        assertEquals(expectedTotal, calculatedTotal, 0.01);

        // Test with additional charges
        item1.setAdditionalCharges(1.00); // Extra sauce
        item2.setAdditionalCharges(0.50); // Extra salt

        // Updated expected total: 13.46 + 1.00 + 0.50 = 14.96
        double updatedExpectedTotal = 14.96;
        double updatedCalculatedTotal = order.calculateTotal();

        assertEquals(updatedExpectedTotal, updatedCalculatedTotal, 0.01);
    }

    @Test
    void testToString() {
        Order order = new Order(customer, restaurant);
        order.setOrderId(1L);
        order.setBranch(branch);

        String toString = order.toString();

        assertTrue(toString.contains("orderId=1"));
        assertTrue(toString.contains("customer=" + customer.getCustomerId()));
        assertTrue(toString.contains("restaurant=" + restaurant.getRestaurantId()));
        assertTrue(toString.contains("branch=" + branch.getBranchId()));
        assertTrue(toString.contains("status=NEW"));
    }

    @Test
    void testValidation() {
        // Empty order is not valid - requires customer and restaurant
        Order emptyOrder = new Order();
        assertFalse(validator.validate(emptyOrder).isEmpty());

        // Order with only customer is not valid
        Order customerOnlyOrder = new Order();
        customerOnlyOrder.setCustomer(customer);
        assertFalse(validator.validate(customerOnlyOrder).isEmpty());

        // Order with only restaurant is not valid
        Order restaurantOnlyOrder = new Order();
        restaurantOnlyOrder.setRestaurant(restaurant);
        assertFalse(validator.validate(restaurantOnlyOrder).isEmpty());

        // Valid order with customer and restaurant
        Order validOrder = new Order(customer, restaurant);
        assertEquals(0, validator.validate(validOrder).size());
    }

    @Test
    void testBranchRelationship() {
        Order order = new Order(customer, restaurant);

        // Initially null
        assertNull(order.getBranch());

        // Set branch
        order.setBranch(branch);
        assertEquals(branch, order.getBranch());
        assertTrue(branch.getOrders().contains(order));

        // Remove from order's side
        order.setBranch(null);
        assertNull(order.getBranch());
        assertFalse(branch.getOrders().contains(order));

        // Add back and remove from branch's side
        order.setBranch(branch);
        branch.removeOrder(order);
        assertNull(order.getBranch());
        assertFalse(branch.getOrders().contains(order));
    }

    @Test
    void testCustomerRestaurantRelationships() {
        // Test changing customer
        Order order = new Order();
        order.setCustomer(customer);
        assertTrue(customer.getOrders().contains(order));

        Customer newCustomer = new Customer("Jane Doe", "jane@example.com", "+19876543210");
        order.setCustomer(newCustomer);
        assertFalse(customer.getOrders().contains(order));
        assertTrue(newCustomer.getOrders().contains(order));

        // Test changing restaurant
        order.setRestaurant(restaurant);
        assertTrue(restaurant.getOrders().contains(order));

        Restaurant newRestaurant = new Restaurant("Pizza Hut", "Pizza");
        order.setRestaurant(newRestaurant);
        assertFalse(restaurant.getOrders().contains(order));
        assertTrue(newRestaurant.getOrders().contains(order));
    }

    @Test
    void testOrderTimeManagement() {
        // Test default order time is set
        Order order = new Order();
        assertNotNull(order.getOrderTime());

        // Test order time can be modified
        Date newOrderTime = new Date(System.currentTimeMillis() - 86400000); // Yesterday
        order.setOrderTime(newOrderTime);
        assertEquals(newOrderTime, order.getOrderTime());
    }
}