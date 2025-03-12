package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testConstructors() {
        // Default constructor
        OrderItem item1 = new OrderItem();
        assertNull(item1.getOrderItemId());
        assertNull(item1.getMenuItemId());
        assertEquals(1, item1.getQuantity());
        assertEquals(0.0, item1.getPrice());

        // Constructor with orderItemId and quantity
        OrderItem item2 = new OrderItem(1L, 3);
        assertEquals(1L, item2.getOrderItemId());
        assertEquals(3, item2.getQuantity());

        // Constructor with menuItemId, quantity, price
        OrderItem item3 = new OrderItem(101L, 2, 9.99);
        assertNull(item3.getOrderItemId());
        assertEquals(101L, item3.getMenuItemId());
        assertEquals(2, item3.getQuantity());
        assertEquals(9.99, item3.getPrice());

        // Constructor with menuItemId, name, quantity, price
        OrderItem item4 = new OrderItem(102L, "Burger", 1, 5.99);
        assertNull(item4.getOrderItemId());
        assertEquals(102L, item4.getMenuItemId());
        assertEquals("Burger", item4.getMenuItemName());
        assertEquals(1, item4.getQuantity());
        assertEquals(5.99, item4.getPrice());

        // Constructor with orderItemId, menuItemId, name, quantity, price
        OrderItem item5 = new OrderItem(1L, 103L, "Fries", 2, 2.99);
        assertEquals(1L, item5.getOrderItemId());
        assertEquals(103L, item5.getMenuItemId());
        assertEquals("Fries", item5.getMenuItemName());
        assertEquals(2, item5.getQuantity());
        assertEquals(2.99, item5.getPrice());
    }

    @Test
    void testSetters() {
        OrderItem item = new OrderItem();

        item.setOrderItemId(1L);
        item.setMenuItemId(101L);
        item.setMenuItemName("Pizza");
        item.setQuantity(2);
        item.setPrice(12.99);
        item.setAdditionalCharges(1.50);

        assertEquals(1L, item.getOrderItemId());
        assertEquals(101L, item.getMenuItemId());
        assertEquals("Pizza", item.getMenuItemName());
        assertEquals(2, item.getQuantity());
        assertEquals(12.99, item.getPrice());
        assertEquals(1.50, item.getAdditionalCharges());
    }

    @Test
    void testOrderRelationship() {
        Order order = new Order();
        OrderItem item = new OrderItem(101L, "Burger", 1, 5.99);

        // Test setting order
        item.setOrder(order);
        assertEquals(order, item.getOrder());
    }

    @Test
    void testNegativeValueHandling() {
        OrderItem item = new OrderItem();

        // Quantity cannot be less than 1
        item.setQuantity(-3);
        assertEquals(1, item.getQuantity());

        // Price cannot be negative
        item.setPrice(-10.00);
        assertEquals(0.0, item.getPrice());

        // Additional charges cannot be negative
        item.setAdditionalCharges(-5.00);
        assertEquals(0.0, item.getAdditionalCharges());
    }

    @Test
    void testGetTotalPrice() {
        OrderItem item = new OrderItem(101L, "Pizza", 2, 10.00);

        // No additional charges: 2 * 10.00 = 20.00
        assertEquals(20.00, item.getTotalPrice());

        // Add additional charges: 2 * 10.00 + 2.50 = 22.50
        item.setAdditionalCharges(2.50);
        assertEquals(22.50, item.getTotalPrice());
    }

    @Test
    void testSpecialInstructions() {
        OrderItem item = new OrderItem(101L, "Pizza", 1, 10.00);

        // Initial state
        assertTrue(item.getSpecialInstructions().isEmpty());

        // Add instructions
        item.addSpecialInstruction("No onions");
        item.addSpecialInstruction("Extra cheese");

        List<String> expected = Arrays.asList("No onions", "Extra cheese");
        assertEquals(expected, item.getSpecialInstructions());
        assertEquals(2, item.getSpecialInstructions().size());

        // Remove instruction
        item.removeSpecialInstruction("No onions");
        assertEquals(1, item.getSpecialInstructions().size());
        assertEquals("Extra cheese", item.getSpecialInstructions().get(0));

        // Set all instructions
        List<String> newInstructions = Arrays.asList("Well done", "Cut in 6 slices");
        item.setSpecialInstructions(newInstructions);
        assertEquals(newInstructions, item.getSpecialInstructions());

        // Null handling
        item.setSpecialInstructions(null);
        assertNotNull(item.getSpecialInstructions());
        assertTrue(item.getSpecialInstructions().isEmpty());
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
    void testValidation() {
        // Menu item ID is required
        OrderItem invalidItem = new OrderItem();
        invalidItem.setQuantity(1);
        invalidItem.setPrice(10.00);
        assertFalse(validator.validate(invalidItem).isEmpty());

        // Valid item
        OrderItem validItem = new OrderItem(101L, 1, 10.00);
        assertEquals(0, validator.validate(validItem).size());
    }
}