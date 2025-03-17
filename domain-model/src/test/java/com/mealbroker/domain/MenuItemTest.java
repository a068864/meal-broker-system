package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MenuItem class
 */
@DisplayName("MenuItem")
public class MenuItemTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private MenuItem menuItem;
    private Menu menu;

    @BeforeEach
    void setUp() {
        menuItem = new MenuItem("Burger", "Classic beef burger", 5.99);
        menu = new Menu(1L);
    }

    @Test
    void testConstructors() {
        MenuItem defaultItem = new MenuItem();
        assertNull(defaultItem.getMenuItemId());
        assertNull(defaultItem.getName());
        assertNull(defaultItem.getDescription());
        assertEquals(0.0, defaultItem.getPrice());
        assertTrue(defaultItem.isAvailable()); // Default available
        assertEquals(100, defaultItem.getStock()); // Default stock
        assertNotNull(defaultItem.getAllergens());
        assertTrue(defaultItem.getAllergens().isEmpty());

        String name = "Burger";
        String description = "Classic beef burger";
        double price = 5.99;
        MenuItem item = new MenuItem(name, description, price);

        assertNull(item.getMenuItemId());
        assertEquals(name, item.getName());
        assertEquals(description, item.getDescription());
        assertEquals(price, item.getPrice());
        assertTrue(item.isAvailable()); // Default available
        assertEquals(100, item.getStock()); // Default stock

        Long id = 42L;
        MenuItem item2 = new MenuItem(id, name, description, price);
        assertEquals(id, item2.getMenuItemId());
        assertEquals(name, item2.getName());
        assertEquals(description, item2.getDescription());
        assertEquals(price, item2.getPrice());
        assertTrue(item2.isAvailable()); // Default available
        assertEquals(100, item2.getStock()); // Default stock
    }

    @Test
    void testSetMenuItemId() {
        Long id = 42L;
        menuItem.setMenuItemId(id);
        assertEquals(id, menuItem.getMenuItemId());
    }

    @Test
    void testSetName() {
        String name = "Cheeseburger";
        menuItem.setName(name);
        assertEquals(name, menuItem.getName());
    }

    @Test
    void testSetDescription() {
        String description = "Delicious burger with cheese";
        menuItem.setDescription(description);
        assertEquals(description, menuItem.getDescription());
    }

    @Test
    void testSetPrice() {
        double price = 6.99;
        menuItem.setPrice(price);
        assertEquals(price, menuItem.getPrice());
    }

    @Test
    void testSetAvailable() {
        // Default is true
        assertTrue(menuItem.isAvailable());

        // Set to false
        menuItem.setAvailable(false);
        assertFalse(menuItem.isAvailable());

        // Set to true again
        menuItem.setAvailable(true);
        assertTrue(menuItem.isAvailable());
    }

    @Test
    void testSetStock() {
        int stock = 50;
        menuItem.setStock(stock);
        assertEquals(stock, menuItem.getStock());
    }

    @Test
    void testMenuRelationship() {
        menuItem.setMenu(menu);
        assertEquals(menu, menuItem.getMenu());
        assertTrue(menu.getItems().contains(menuItem));

        // Test with a new menu
        Menu newMenu = new Menu(2L);
        menuItem.setMenu(newMenu);
        assertEquals(newMenu, menuItem.getMenu());
        assertTrue(newMenu.getItems().contains(menuItem));
        assertFalse(menu.getItems().contains(menuItem));

        // Test with null
        menuItem.setMenu(null);
        assertNull(menuItem.getMenu());
        assertFalse(newMenu.getItems().contains(menuItem));
    }

    @Test
    void testReduceStock() {
        // Initial stock is 100
        assertEquals(100, menuItem.getStock());
        assertTrue(menuItem.isAvailable());

        // Reduce by 30
        boolean result1 = menuItem.reduceStock(30);
        assertTrue(result1);
        assertEquals(70, menuItem.getStock());
        assertTrue(menuItem.isAvailable());

        // Reduce by 50
        boolean result2 = menuItem.reduceStock(50);
        assertTrue(result2);
        assertEquals(20, menuItem.getStock());
        assertTrue(menuItem.isAvailable());

        // Reduce by 25 (more than available, should cap at 0 and set available to false)
        boolean result3 = menuItem.reduceStock(25);
        assertFalse(result3);
        assertEquals(0, menuItem.getStock());
        assertFalse(menuItem.isAvailable());

        // Further reduction should not go below 0
        boolean result4 = menuItem.reduceStock(10);
        assertFalse(result4);
        assertEquals(0, menuItem.getStock());
        assertFalse(menuItem.isAvailable());

        // Test with negative or zero quantity (should return true and not change stock)
        boolean result5 = menuItem.reduceStock(0);
        assertTrue(result5);
        assertEquals(0, menuItem.getStock());

        boolean result6 = menuItem.reduceStock(-5);
        assertTrue(result6);
        assertEquals(0, menuItem.getStock());
    }

    @Test
    void testAddAllergen() {
        // Initial state
        assertTrue(menuItem.getAllergens().isEmpty());

        // Add allergens
        menuItem.addAllergen("Gluten");
        menuItem.addAllergen("Dairy");

        List<String> expected = Arrays.asList("Gluten", "Dairy");
        assertEquals(expected, menuItem.getAllergens());
        assertEquals(2, menuItem.getAllergens().size());
    }

    @Test
    void testRemoveAllergen() {
        // Add some allergens first
        menuItem.addAllergen("Gluten");
        menuItem.addAllergen("Dairy");
        menuItem.addAllergen("Eggs");

        // Remove an allergen
        menuItem.removeAllergen("Gluten");
        assertEquals(2, menuItem.getAllergens().size());
        assertFalse(menuItem.getAllergens().contains("Gluten"));
        assertTrue(menuItem.getAllergens().contains("Dairy"));
        assertTrue(menuItem.getAllergens().contains("Eggs"));

        // Try removing an allergen that doesn't exist
        menuItem.removeAllergen("Nuts");
        assertEquals(2, menuItem.getAllergens().size());
    }

    @Test
    void testSetAllergens() {
        // Add some allergens first
        menuItem.addAllergen("Gluten");
        menuItem.addAllergen("Dairy");

        // Set new allergens
        List<String> newAllergens = Arrays.asList("Eggs", "Soy", "Nuts");
        menuItem.setAllergens(newAllergens);

        assertEquals(newAllergens, menuItem.getAllergens());
        assertEquals(3, menuItem.getAllergens().size());

        // Test with null (should set empty list)
        menuItem.setAllergens(null);
        assertNotNull(menuItem.getAllergens());
        assertTrue(menuItem.getAllergens().isEmpty());
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
    void testValidMenuItem() {
        MenuItem validItem = new MenuItem("Burger", "Description", 5.99);
        assertEquals(0, validator.validate(validItem).size());
    }

    @Test
    void testInvalidMenuItemNullName() {
        MenuItem invalidItem = new MenuItem(null, "Description", 5.99);
        assertFalse(validator.validate(invalidItem).isEmpty());
    }

    @Test
    void testInvalidMenuItemNegativePrice() {
        MenuItem negativePriceItem = new MenuItem("Burger", "Description", -5.99);
        assertFalse(validator.validate(negativePriceItem).isEmpty());
    }
}