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
 * Unit tests for the Menu class
 */
@DisplayName("Menu")
public class MenuTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private Menu menu;
    private Branch branch;
    private MenuItem item1;
    private MenuItem item2;

    @BeforeEach
    void setUp() {
        menu = new Menu(1L);
        branch = new Branch("Downtown", new Location(40.7128, -74.0060));

        item1 = new MenuItem("Burger", "Classic beef burger", 5.99);
        item1.setMenuItemId(101L);

        item2 = new MenuItem("Fries", "Golden french fries", 2.99);
        item2.setMenuItemId(102L);
    }

    @Test
    void testConstructors() {
        Menu defaultMenu = new Menu();
        assertNull(defaultMenu.getMenuId());
        assertNotNull(defaultMenu.getItems());
        assertTrue(defaultMenu.getItems().isEmpty());

        Long id = 5L;
        Menu idMenu = new Menu(id);
        assertEquals(id, idMenu.getMenuId());
        assertNotNull(idMenu.getItems());
        assertTrue(idMenu.getItems().isEmpty());
    }

    @Test
    void testSetMenuId() {
        Long newId = 42L;
        menu.setMenuId(newId);
        assertEquals(newId, menu.getMenuId());
    }

    @Test
    void testSetBranch() {
        menu.setBranch(branch);
        assertEquals(branch, menu.getBranch());
        assertEquals(menu, branch.getMenu());

        // Test with a new branch
        Branch newBranch = new Branch("Uptown", new Location(43.6532, -79.3832));
        menu.setBranch(newBranch);
        assertEquals(newBranch, menu.getBranch());
        assertEquals(menu, newBranch.getMenu());
        assertNull(branch.getMenu());

        // Test with null
        menu.setBranch(null);
        assertNull(menu.getBranch());
        assertNull(newBranch.getMenu());
    }

    @Test
    void testAddItem() {
        menu.addItem(item1);
        menu.addItem(item2);

        assertEquals(2, menu.getItems().size());
        assertTrue(menu.getItems().contains(item1));
        assertTrue(menu.getItems().contains(item2));
        assertEquals(menu, item1.getMenu());
        assertEquals(menu, item2.getMenu());

        // Try adding the same item again (should not duplicate)
        menu.addItem(item1);
        assertEquals(2, menu.getItems().size());
    }

    @Test
    void testRemoveItem() {
        menu.addItem(item1);
        menu.addItem(item2);

        menu.removeItem(item1);
        assertEquals(1, menu.getItems().size());
        assertFalse(menu.getItems().contains(item1));
        assertTrue(menu.getItems().contains(item2));
        assertNull(item1.getMenu());
        assertEquals(menu, item2.getMenu());

        // Try removing an item that's not in the menu
        MenuItem item3 = new MenuItem("Cola", "Ice cold cola", 1.99);
        menu.removeItem(item3);
        assertEquals(1, menu.getItems().size());
    }

    @Test
    void testGetItem() {
        menu.addItem(item1);
        menu.addItem(item2);

        assertEquals(item1, menu.getItem(item1.getMenuItemId()));
        assertEquals(item2, menu.getItem(item2.getMenuItemId()));
        assertNull(menu.getItem(999L));
    }

    @Test
    void testSetItems() {
        menu.addItem(item1);

        List<MenuItem> newItems = Arrays.asList(item2);
        menu.setItems(newItems);

        assertEquals(1, menu.getItems().size());
        assertFalse(menu.getItems().contains(item1));
        assertTrue(menu.getItems().contains(item2));
        assertNull(item1.getMenu());
        assertEquals(menu, item2.getMenu());

        // Test with null
        menu.setItems(null);
        assertEquals(0, menu.getItems().size());
    }

    @Test
    void testUpdateItemAvailability() {
        menu.addItem(item1);
        menu.addItem(item2);

        assertTrue(item1.isAvailable());
        menu.updateItemAvailability(item1.getMenuItemId(), false);
        assertFalse(item1.isAvailable());
        assertTrue(item2.isAvailable());

        // Try with non-existent item ID
        menu.updateItemAvailability(999L, false);
        // Should not throw any exception
    }

    @Test
    void testGetAvailableItems() {
        menu.addItem(item1);
        menu.addItem(item2);

        assertEquals(2, menu.getAvailableItems().size());

        item1.setAvailable(false);
        List<MenuItem> availableItems = menu.getAvailableItems();
        assertEquals(1, availableItems.size());
        assertFalse(availableItems.contains(item1));
        assertTrue(availableItems.contains(item2));
    }

    @Test
    void testToString() {
        menu.addItem(item1);
        menu.addItem(item2);

        String toString = menu.toString();
        assertTrue(toString.contains("menuId=1"));
        assertTrue(toString.contains("itemCount=2"));
    }

    @Test
    void testValidation() {
        assertEquals(0, validator.validate(menu).size());
    }
}