package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Branch class
 */
@DisplayName("Branch")
public class BranchTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private Branch branch;
    private Location location;
    private Restaurant restaurant;
    private Menu menu;

    @BeforeEach
    void setUp() {
        location = new Location(43.6532, -79.3832);
        branch = new Branch("Downtown", location);
        restaurant = new Restaurant("Test Restaurant", "Test Cuisine");
        menu = new Menu(1L);
    }

    @Test
    void testConstructors() {
        Branch defaultBranch = new Branch();
        assertNull(defaultBranch.getBranchId());
        assertNull(defaultBranch.getBranchName());
        assertNull(defaultBranch.getLocation());
        assertTrue(defaultBranch.isActive()); // Default active state
        assertEquals(10, defaultBranch.getOperatingRadius()); // Default radius

        String name = "Test Branch";
        Location loc = new Location(40.7128, -74.0060);
        Branch nameBranch = new Branch(name, loc);

        assertNull(nameBranch.getBranchId());
        assertEquals(name, nameBranch.getBranchName());
        assertEquals(loc, nameBranch.getLocation());
        assertTrue(nameBranch.isActive()); // Default active state
        assertEquals(10, nameBranch.getOperatingRadius()); // Default radius

        Long id = 42L;
        Branch fullBranch = new Branch(id, name, loc);

        assertEquals(id, fullBranch.getBranchId());
        assertEquals(name, fullBranch.getBranchName());
        assertEquals(loc, fullBranch.getLocation());
        assertTrue(fullBranch.isActive()); // Default active state
        assertEquals(10, fullBranch.getOperatingRadius()); // Default radius
    }

    @Test
    void testSetBranchId() {
        Long id = 42L;
        branch.setBranchId(id);
        assertEquals(id, branch.getBranchId());
    }

    @Test
    void testSetBranchName() {
        String name = "New Branch Name";
        branch.setBranchName(name);
        assertEquals(name, branch.getBranchName());
    }

    @Test
    void testSetLocation() {
        Location newLocation = new Location(40.7128, -74.0060);
        branch.setLocation(newLocation);
        assertEquals(newLocation, branch.getLocation());
    }

    @Test
    void testSetAddress() {
        String address = "123 Main St, Toronto, ON";
        branch.setAddress(address);
        assertEquals(address, branch.getAddress());
    }

    @Test
    void testSetActive() {
        // Default is true
        assertTrue(branch.isActive());

        // Set to false
        branch.setActive(false);
        assertFalse(branch.isActive());

        // Set to true again
        branch.setActive(true);
        assertTrue(branch.isActive());

        // Set to null (should default to true)
        branch.setActive(null);
        assertTrue(branch.isActive());
    }

    @Test
    void testSetOperatingRadius() {
        Integer radius = 20;
        branch.setOperatingRadius(radius);
        assertEquals(radius, branch.getOperatingRadius());
    }

    @Test
    void testSetRestaurant() {
        branch.setRestaurant(restaurant);
        assertEquals(restaurant, branch.getRestaurant());
        assertTrue(restaurant.getBranches().contains(branch));
        assertEquals(restaurant.getRestaurantId(), branch.getRestaurantId());

        // Test with a new restaurant
        Restaurant newRestaurant = new Restaurant("New Restaurant", "New Cuisine");
        branch.setRestaurant(newRestaurant);
        assertEquals(newRestaurant, branch.getRestaurant());
        assertTrue(newRestaurant.getBranches().contains(branch));
        assertFalse(restaurant.getBranches().contains(branch));

        // Test with null
        branch.setRestaurant(null);
        assertNull(branch.getRestaurant());
        assertFalse(newRestaurant.getBranches().contains(branch));
    }

    @Test
    void testSetMenu() {
        branch.setMenu(menu);
        assertEquals(menu, branch.getMenu());
        assertEquals(branch, menu.getBranch());

        // Test with a new menu
        Menu newMenu = new Menu(2L);
        branch.setMenu(newMenu);
        assertEquals(newMenu, branch.getMenu());
        assertEquals(branch, newMenu.getBranch());
        assertNull(menu.getBranch());

        // Test with null
        branch.setMenu(null);
        assertNull(branch.getMenu());
        assertNull(newMenu.getBranch());
    }

    @Test
    void testAddOrder() {
        Customer customer = new Customer("John Doe", "john@example.com", "+12345678901");
        Order order1 = new Order(customer, restaurant);
        Order order2 = new Order(customer, restaurant);
        branch.setRestaurant(restaurant);

        branch.addOrder(order1);
        branch.addOrder(order2);

        assertEquals(2, branch.getOrders().size());
        assertTrue(branch.getOrders().contains(order1));
        assertTrue(branch.getOrders().contains(order2));
        assertEquals(branch, order1.getBranch());
        assertEquals(branch, order2.getBranch());

        // Try adding the same order again (should not duplicate)
        branch.addOrder(order1);
        assertEquals(2, branch.getOrders().size());
    }

    @Test
    void testRemoveOrder() {
        Customer customer = new Customer("John Doe", "john@example.com", "+12345678901");
        Order order1 = new Order(customer, restaurant);
        Order order2 = new Order(customer, restaurant);
        branch.setRestaurant(restaurant);

        branch.addOrder(order1);
        branch.addOrder(order2);

        branch.removeOrder(order1);
        assertEquals(1, branch.getOrders().size());
        assertFalse(branch.getOrders().contains(order1));
        assertTrue(branch.getOrders().contains(order2));
        assertNull(order1.getBranch());
        assertEquals(branch, order2.getBranch());

        // Try removing non-existent order
        Order order3 = new Order(customer, restaurant);
        branch.removeOrder(order3);
        assertEquals(1, branch.getOrders().size());
    }

    @Test
    void testToString() {
        Branch testBranch = new Branch(1L, "Downtown", location);
        testBranch.setAddress("123 Main St, Toronto, ON");
        testBranch.setActive(true);

        String toString = testBranch.toString();
        assertTrue(toString.contains("branchId=1"));
        assertTrue(toString.contains("branchName='Downtown'"));
        assertTrue(toString.contains("address='123 Main St, Toronto, ON'"));
        assertTrue(toString.contains("isActive=true"));
    }

    @Test
    void testValidBranch() {
        Branch validBranch = new Branch("Downtown", location);
        assertEquals(0, validator.validate(validBranch).size());
    }

    @Test
    void testInvalidBranchNullName() {
        Branch invalidBranch = new Branch();
        invalidBranch.setLocation(location);
        assertFalse(validator.validate(invalidBranch).isEmpty());
    }

    @Test
    void testInvalidBranchEmptyName() {
        Branch invalidBranch = new Branch("", location);
        assertFalse(validator.validate(invalidBranch).isEmpty());
    }

    @Test
    void testInvalidBranchNullLocation() {
        Branch invalidBranch = new Branch("Downtown", null);
        assertFalse(validator.validate(invalidBranch).isEmpty());
    }
}