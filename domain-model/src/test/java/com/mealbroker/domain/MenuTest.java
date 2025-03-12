package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MenuTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testConstructors() {
        // Default constructor
        Menu menu1 = new Menu();
        assertNull(menu1.getMenuId());
        assertNotNull(menu1.getItems());
        assertTrue(menu1.getItems().isEmpty());

        // Constructor with ID
        Menu menu2 = new Menu(1L);
        assertEquals(1L, menu2.getMenuId());
        assertNotNull(menu2.getItems());
        assertTrue(menu2.getItems().isEmpty());
    }

    @Test
    void testSetters() {
        Menu menu = new Menu();

        menu.setMenuId(1L);
        assertEquals(1L, menu.getMenuId());

        Branch branch = new Branch("Downtown", new Location(40.7128, -74.0060));
        menu.setBranch(branch);
        assertEquals(branch, menu.getBranch());
    }

    @Test
    void testItemOperations() {
        Menu menu = new Menu(1L);

        // Test initial state
        assertTrue(menu.getItems().isEmpty());

        // Create items
        MenuItem item1 = new MenuItem("Burger", "Classic burger", 5.99);
        MenuItem item2 = new MenuItem("Fries", "Golden french fries", 2.99);
        MenuItem item3 = new MenuItem("Cola", "Ice cold cola", 1.99);

        // Add items
        menu.addItem(item1);
        menu.addItem(item2);

        assertEquals(2, menu.getItems().size());
        assertTrue(menu.getItems().contains(item1));
        assertTrue(menu.getItems().contains(item2));
        assertEquals(menu, item1.getMenu());
        assertEquals(menu, item2.getMenu());

        // Get item by ID
        item1.setMenuItemId(101L);
        item2.setMenuItemId(102L);

        assertEquals(item1, menu.getItem(101L));
        assertEquals(item2, menu.getItem(102L));
        assertNull(menu.getItem(999L));

        // Update item availability
        menu.updateItemAvailability(101L, false);
        assertFalse(item1.isAvailable());
        assertTrue(item2.isAvailable());

        // Available items
        assertEquals(1, menu.getAvailableItems().size());
        assertFalse(menu.getAvailableItems().contains(item1));
        assertTrue(menu.getAvailableItems().contains(item2));

        // Set items
        menu.setItems(null);
        assertEquals(0, menu.getItems().size());

        // Add multiple items
        menu.addItem(item1);
        menu.addItem(item2);
        menu.addItem(item3);
        assertEquals(3, menu.getItems().size());
    }

    @Test
    void testBranchRelationship() {
        Menu menu = new Menu(1L);
        Branch branch = new Branch("Downtown", new Location(40.7128, -74.0060));

        // Test bidirectional relationship
        branch.setMenu(menu);
        assertEquals(branch, menu.getBranch());
        assertEquals(menu, branch.getMenu());

        // Test changing branch
        Branch newBranch = new Branch("Uptown", new Location(40.8075, -73.9626));
        menu.setBranch(newBranch);
        assertEquals(newBranch, menu.getBranch());
    }

    @Test
    void testToString() {
        Menu menu = new Menu(1L);

        // Add some items
        MenuItem item1 = new MenuItem("Burger", "Classic burger", 5.99);
        MenuItem item2 = new MenuItem("Fries", "Golden french fries", 2.99);
        menu.addItem(item1);
        menu.addItem(item2);

        String toString = menu.toString();

        assertTrue(toString.contains("menuId=1"));
        assertTrue(toString.contains("itemCount=2"));
    }

    @Test
    void testValidation() {
        // Menus don't have specific validation beyond the basic entity validation
        Menu menu = new Menu(1L);
        assertEquals(0, validator.validate(menu).size());
    }
}