package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private OrderItem orderItem;
    private Order order;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem(101L, "Pizza", 2, 10.00);
        Customer customer = new Customer("John Doe", "john@example.com", "+12345678901");
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Cuisine");
        order = new Order(customer, restaurant);
    }

    @Test
    void testConstructors() {
        OrderItem defaultItem = new OrderItem();
        assertNull(defaultItem.getOrderItemId());
        assertNull(defaultItem.getMenuItemId());
        assertNull(defaultItem.getMenuItemName());
        assertEquals(1, defaultItem.getQuantity()); // Default quantity
        assertEquals(0.0, defaultItem.getPrice());
        assertEquals(0.0, defaultItem.getAdditionalCharges());
        assertNotNull(defaultItem.getSpecialInstructions());
        assertTrue(defaultItem.getSpecialInstructions().isEmpty());

        Long orderItemId = 42L;
        int quantity = 3;
        OrderItem item = new OrderItem(orderItemId, quantity);

        assertEquals(orderItemId, item.getOrderItemId());
        assertEquals(quantity, item.getQuantity());
        assertNull(item.getMenuItemId());
        assertEquals(0.0, item.getPrice());
        assertEquals(0.0, item.getAdditionalCharges());

        Long menuItemId = 101L;
        double price = 9.99;
        OrderItem item2 = new OrderItem(menuItemId, quantity, price);

        assertNull(item2.getOrderItemId());
        assertEquals(menuItemId, item2.getMenuItemId());
        assertEquals(quantity, item2.getQuantity());
        assertEquals(price, item2.getPrice());
        assertEquals(0.0, item2.getAdditionalCharges());
    }

    @Test
    void testMenuItemIdNameQuantityPriceConstructor() {
        Long menuItemId = 102L;
        String name = "Burger";
        int quantity = 1;
        double price = 5.99;
        OrderItem item = new OrderItem(menuItemId, name, quantity, price);

        assertNull(item.getOrderItemId());
        assertEquals(menuItemId, item.getMenuItemId());
        assertEquals(name, item.getMenuItemName());
        assertEquals(quantity, item.getQuantity());
        assertEquals(price, item.getPrice());
        assertEquals(0.0, item.getAdditionalCharges());
    }

    @Test
    void testFullConstructor() {
        Long orderItemId = 1L;
        Long menuItemId = 103L;
        String name = "Fries";
        int quantity = 2;
        double price = 2.99;
        OrderItem item = new OrderItem(orderItemId, menuItemId, name, quantity, price);

        assertEquals(orderItemId, item.getOrderItemId());
        assertEquals(menuItemId, item.getMenuItemId());
        assertEquals(name, item.getMenuItemName());
        assertEquals(quantity, item.getQuantity());
        assertEquals(price, item.getPrice());
        assertEquals(0.0, item.getAdditionalCharges());
    }

    @Test
    void testSetOrderItemId() {
        Long id = 42L;
        orderItem.setOrderItemId(id);
        assertEquals(id, orderItem.getOrderItemId());
    }

    @Test
    void testSetMenuItemId() {
        Long id = 42L;
        orderItem.setMenuItemId(id);
        assertEquals(id, orderItem.getMenuItemId());
    }

    @Test
    void testSetMenuItemName() {
        String name = "Cheeseburger";
        orderItem.setMenuItemName(name);
        assertEquals(name, orderItem.getMenuItemName());
    }

    @Test
    void testSetQuantity() {
        int quantity = 3;
        orderItem.setQuantity(quantity);
        assertEquals(quantity, orderItem.getQuantity());
    }

    @Test
    void testSetPrice() {
        double price = 15.99;
        orderItem.setPrice(price);
        assertEquals(price, orderItem.getPrice());
    }

    @Test
    void testSetAdditionalCharges() {
        double charges = 2.50;
        orderItem.setAdditionalCharges(charges);
        assertEquals(charges, orderItem.getAdditionalCharges());
    }

    @Test
    void testOrderRelationship() {
        orderItem.setOrder(order);
        assertEquals(order, orderItem.getOrder());
        assertTrue(order.getItems().contains(orderItem));

        // Test with a new order
        Customer newCustomer = new Customer("Jane Doe", "jane@example.com", "+12345678902");
        Restaurant newRestaurant = new Restaurant("New Restaurant", "New Cuisine");
        Order newOrder = new Order(newCustomer, newRestaurant);

        orderItem.setOrder(newOrder);
        assertEquals(newOrder, orderItem.getOrder());
        assertTrue(newOrder.getItems().contains(orderItem));
        assertFalse(order.getItems().contains(orderItem));

        // Test with null
        orderItem.setOrder(null);
        assertNull(orderItem.getOrder());
        assertFalse(newOrder.getItems().contains(orderItem));
    }

    @Test
    void testGetTotalPrice() {
        // Set up order item with price, quantity, and additional charges
        OrderItem item = new OrderItem(101L, "Pizza", 2, 10.00);

        // Without additional charges: 2 * 10.00 = 20.00
        assertEquals(20.00, item.getTotalPrice());

        // With additional charges: 2 * 10.00 + 2.50 = 22.50
        item.setAdditionalCharges(2.50);
        assertEquals(22.50, item.getTotalPrice());

        // Change quantity: 3 * 10.00 + 2.50 = 32.50
        item.setQuantity(3);
        assertEquals(32.50, item.getTotalPrice());

        // Change price: 3 * 12.00 + 2.50 = 38.50
        item.setPrice(12.00);
        assertEquals(38.50, item.getTotalPrice());
    }

    @Test
    void testAddSpecialInstruction() {
        // Initial state
        assertTrue(orderItem.getSpecialInstructions().isEmpty());

        // Add instructions
        orderItem.addSpecialInstruction("No onions");
        orderItem.addSpecialInstruction("Extra cheese");

        List<String> expected = Arrays.asList("No onions", "Extra cheese");
        assertEquals(expected, orderItem.getSpecialInstructions());
        assertEquals(2, orderItem.getSpecialInstructions().size());

        // Test adding null or empty instruction (should be ignored)
        orderItem.addSpecialInstruction(null);
        orderItem.addSpecialInstruction("");
        orderItem.addSpecialInstruction("  ");
        assertEquals(2, orderItem.getSpecialInstructions().size());
    }

    @Test
    void testRemoveSpecialInstruction() {
        // Add some instructions first
        orderItem.addSpecialInstruction("No onions");
        orderItem.addSpecialInstruction("Extra cheese");
        orderItem.addSpecialInstruction("Well done");

        // Remove an instruction
        orderItem.removeSpecialInstruction("No onions");
        assertEquals(2, orderItem.getSpecialInstructions().size());
        assertFalse(orderItem.getSpecialInstructions().contains("No onions"));
        assertTrue(orderItem.getSpecialInstructions().contains("Extra cheese"));
        assertTrue(orderItem.getSpecialInstructions().contains("Well done"));

        // Try removing an instruction that doesn't exist
        orderItem.removeSpecialInstruction("Gluten free");
        assertEquals(2, orderItem.getSpecialInstructions().size());
    }

    @Test
    void testSetSpecialInstructions() {
        // Add some instructions first
        orderItem.addSpecialInstruction("No onions");
        orderItem.addSpecialInstruction("Extra cheese");

        // Set new instructions
        List<String> newInstructions = Arrays.asList("Well done", "Cut in 6 slices");
        orderItem.setSpecialInstructions(newInstructions);

        assertEquals(newInstructions, orderItem.getSpecialInstructions());
        assertEquals(2, orderItem.getSpecialInstructions().size());

        // Test with null (should set empty list)
        orderItem.setSpecialInstructions(null);
        assertNotNull(orderItem.getSpecialInstructions());
        assertTrue(orderItem.getSpecialInstructions().isEmpty());
    }

    @Test
    void testToString() {
        OrderItem item = new OrderItem(1L, 101L, "Pizza", 2, 10.00);
        item.setAdditionalCharges(2.50);

        String toString = item.toString();
        assertTrue(toString.contains("orderItemId=1"));
        assertTrue(toString.contains("menuItemId=101"));
        assertTrue(toString.contains("menuItemName='Pizza'"));
        assertTrue(toString.contains("quantity=2"));
        assertTrue(toString.contains("price=10.0"));
        assertTrue(toString.contains("additionalCharges=2.5"));
    }

    @Test
    void testValidOrderItem() {
        OrderItem validItem = new OrderItem(101L, 1, 10.00);
        assertEquals(0, validator.validate(validItem).size());
    }

    @Test
    void testInvalidOrderItemNullMenuItemId() {
        OrderItem invalidItem = new OrderItem();
        invalidItem.setQuantity(1);
        invalidItem.setPrice(10.00);
        assertFalse(validator.validate(invalidItem).isEmpty());
    }

    @Test
    void testInvalidOrderItemZeroQuantity() {
        OrderItem invalidItem = new OrderItem(101L, 0, 10.00);
        assertFalse(validator.validate(invalidItem).isEmpty());
    }

    @Test
    void testInvalidOrderItemNegativeQuantity() {
        OrderItem invalidItem = new OrderItem(101L, -1, 10.00);
        assertFalse(validator.validate(invalidItem).isEmpty());
    }

    @Test
    void testInvalidOrderItemNegativePrice() {
        OrderItem invalidItem = new OrderItem(101L, 1, -10.00);
        assertFalse(validator.validate(invalidItem).isEmpty());
    }
}