package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuItemTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testConstructors() {
        // Default constructor
        MenuItem item1 = new MenuItem();
        assertNull(item1.getMenuItemId());
        assertNull(item1.getName());
        assertNull(item1.getDescription());
        assertEquals(0.0, item1.getPrice());
        assertTrue(item1.isAvailable());
        assertEquals(100, item1.getStock());

        // Constructor with basic info
        MenuItem item2 = new MenuItem("Burger", "Classic beef burger", 5.99);
        assertNull(item2.getMenuItemId());
        assertEquals("Burger", item2.getName());
        assertEquals("Classic beef burger", item2.getDescription());
        assertEquals(5.99, item2.getPrice());

        // Constructor with ID and basic info
        MenuItem item3 = new MenuItem(1L, "Fries", "Golden french fries", 2.99);
        assertEquals(1L, item3.getMenuItemId());
        assertEquals("Fries", item3.getName());
        assertEquals("Golden french fries", item3.getDescription());
        assertEquals(2.99, item3.getPrice());
    }

    @Test
    void testSetters() {
        MenuItem item = new MenuItem();

        item.setMenuItemId(1L);
        item.setName("Burger");
        item.setDescription("Classic beef burger");
        item.setPrice(5.99);
        item.setAvailable(true);
        item.setStock(50);

        assertEquals(1L, item.getMenuItemId());
        assertEquals("Burger", item.getName());
        assertEquals("Classic beef burger", item.getDescription());
        assertEquals(5.99, item.getPrice());
        assertTrue(item.isAvailable());
        assertEquals(50, item.getStock());
    }

    @Test
    void testMenuRelationship() {
        MenuItem item = new MenuItem("Burger", "Classic beef burger", 5.99);
        Menu menu = new Menu(1L);

        // Test setting menu
        item.setMenu(menu);
        assertEquals(menu, item.getMenu());
    }

    @Test
    void testNegativeValueHandling() {
        MenuItem item = new MenuItem();

        // Price cannot be negative
        item.setPrice(-5.99);
        assertEquals(0.0, item.getPrice());

        // Stock cannot be negative
        item.setStock(-10);
        assertEquals(0, item.getStock());
    }

    @Test
    void testReduceStock() {
        MenuItem item = new MenuItem("Burger", "Classic beef burger", 5.99);

        // Initial stock is 100
        assertEquals(100, item.getStock());
        assertTrue(item.isAvailable());

        // Reduce by 30
        item.reduceStock(30);
        assertEquals(70, item.getStock());
        assertTrue(item.isAvailable());

        // Reduce by 50
        item.reduceStock(50);
        assertEquals(20, item.getStock());
        assertTrue(item.isAvailable());

        // Reduce by 25 (should cap at 0 and set available to false)
        item.reduceStock(25);
        assertEquals(0, item.getStock());
        assertFalse(item.isAvailable());

        // Further reduction should not go below 0
        item.reduceStock(10);
        assertEquals(0, item.getStock());
    }

    @Test
    void testAllergenOperations() {
        MenuItem item = new MenuItem("Burger", "Classic beef burger", 5.99);

        // Initial state
        assertTrue(item.getAllergens().isEmpty());

        // Add allergens
        item.addAllergen("Gluten");
        item.addAllergen("Dairy");

        List<String> expected = Arrays.asList("Gluten", "Dairy");
        assertEquals(expected, item.getAllergens());

        // Remove allergen
        item.removeAllergen("Gluten");
        assertEquals(1, item.getAllergens().size());
        assertEquals("Dairy", item.getAllergens().get(0));

        // Set allergens
        List<String> newAllergens = Arrays.asList("Eggs", "Soy", "Nuts");
        item.setAllergens(newAllergens);
        assertEquals(newAllergens, item.getAllergens());
    }

    @Test
    void testToString() {
        MenuItem item = new MenuItem(1L, "Burger", "Classic beef burger", 5.99);
        item.setStock(50);
        item.setAvailable(true);

        String toString = item.toString();

        assertTrue(toString.contains("menuItemId=1"));
        assertTrue(toString.contains("name='Burger'"));
        assertTrue(toString.contains("price=5.99"));
        assertTrue(toString.contains("isAvailable=true"));
        assertTrue(toString.contains("stock=50"));
    }

    @Test
    void testValidation() {
        // Name is required
        MenuItem invalidItem = new MenuItem(null, "Description", 5.99);
        assertFalse(validator.validate(invalidItem).isEmpty());

        // Price must be non-negative
        MenuItem negativePriceItem = new MenuItem("Burger", "Description", -5.99);
        assertFalse(validator.validate(negativePriceItem).isEmpty());

        // Valid item
        MenuItem validItem = new MenuItem("Burger", "Description", 5.99);
        assertEquals(0, validator.validate(validItem).size());
    }
}