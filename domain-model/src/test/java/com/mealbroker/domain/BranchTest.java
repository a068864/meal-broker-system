package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BranchTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testConstructors() {
        // Default constructor
        Branch branch1 = new Branch();
        assertNull(branch1.getBranchId());
        assertNull(branch1.getBranchName());
        assertNull(branch1.getLocation());

        // Constructor with name and location
        Location location = new Location(40.7128, -74.0060);
        Branch branch2 = new Branch("Downtown", location);
        assertNull(branch2.getBranchId());
        assertEquals("Downtown", branch2.getBranchName());
        assertEquals(location, branch2.getLocation());

        // Constructor with ID, name and location
        Branch branch3 = new Branch(1L, "Uptown", location);
        assertEquals(1L, branch3.getBranchId());
        assertEquals("Uptown", branch3.getBranchName());
        assertEquals(location, branch3.getLocation());
    }

    @Test
    void testSetters() {
        Branch branch = new Branch();

        branch.setBranchId(1L);
        branch.setBranchName("Downtown");
        Location location = new Location(40.7128, -74.0060);
        branch.setLocation(location);
        branch.setAddress("123 Main St, New York, NY");
        branch.setActive(true);
        branch.setOperatingRadius(5);

        assertEquals(1L, branch.getBranchId());
        assertEquals("Downtown", branch.getBranchName());
        assertEquals(location, branch.getLocation());
        assertEquals("123 Main St, New York, NY", branch.getAddress());
        assertTrue(branch.isActive());
        assertEquals(5, branch.getOperatingRadius());
    }

    @Test
    void testRestaurantRelationship() {
        Branch branch = new Branch("Downtown", new Location(40.7128, -74.0060));
        Restaurant restaurant = new Restaurant("McDonald's", "Fast Food");

        // Test setting restaurant
        branch.setRestaurant(restaurant);
        assertEquals(restaurant, branch.getRestaurant());
        assertEquals(restaurant.getRestaurantId(), branch.getRestaurantId());
    }

    @Test
    void testMenuRelationship() {
        Branch branch = new Branch("Downtown", new Location(40.7128, -74.0060));
        Menu menu = new Menu(1L);

        // Test bidirectional relationship
        branch.setMenu(menu);
        assertEquals(menu, branch.getMenu());
        assertEquals(branch, menu.getBranch());

        // Test setting null
        branch.setMenu(null);
        assertNull(branch.getMenu());
    }

    @Test
    void testOrderOperations() {
        Branch branch = new Branch("Downtown", new Location(40.7128, -74.0060));
        Customer customer = new Customer("John Doe", "john@example.com", "+12345678901");
        Restaurant restaurant = new Restaurant("McDonald's", "Fast Food");
        branch.setRestaurant(restaurant);

        // Initial state
        assertTrue(branch.getOrders().isEmpty());

        // Create orders
        Order order1 = new Order(customer, restaurant);
        Order order2 = new Order(customer, restaurant);

        // Add orders
        branch.addOrder(order1);
        branch.addOrder(order2);

        assertEquals(2, branch.getOrders().size());
        assertTrue(branch.getOrders().contains(order1));
        assertTrue(branch.getOrders().contains(order2));
        assertEquals(branch, order1.getBranch());
        assertEquals(branch, order2.getBranch());

        // Remove order
        branch.removeOrder(order1);

        assertEquals(1, branch.getOrders().size());
        assertFalse(branch.getOrders().contains(order1));
        assertTrue(branch.getOrders().contains(order2));
        assertNull(order1.getBranch());
        assertEquals(branch, order2.getBranch());
    }

    @Test
    void testIsActive() {
        Branch branch = new Branch("Downtown", new Location(40.7128, -74.0060));

        // Default value
        assertTrue(branch.isActive());

        // Set to false
        branch.setActive(false);
        assertFalse(branch.isActive());

        // Debug print to identify potential issues with Boolean/boolean conversion
        System.out.println("Branch active state: " + branch.isActive());
        System.out.println("Branch active field class: " + (branch.isActive() ? branch.getBranchName() : "null"));

        branch.setActive(null);

        // Set back to true
        branch.setActive(true);
        assertTrue(branch.isActive());
    }

    @Test
    void testToString() {
        Branch branch = new Branch(1L, "Downtown", new Location(40.7128, -74.0060));
        branch.setAddress("123 Main St, New York, NY");
        branch.setActive(true);

        String toString = branch.toString();

        assertTrue(toString.contains("branchId=1"));
        assertTrue(toString.contains("branchName='Downtown'"));
        assertTrue(toString.contains("address='123 Main St, New York, NY'"));
        assertTrue(toString.contains("isActive=true"));
    }

    @Test
    void testValidation() {
        // Branch name is required
        Branch invalidBranch1 = new Branch();
        invalidBranch1.setLocation(new Location(40.7128, -74.0060));
        assertFalse(validator.validate(invalidBranch1).isEmpty());

        // Location is required
        Branch invalidBranch2 = new Branch("Downtown", null);
        assertFalse(validator.validate(invalidBranch2).isEmpty());

        // Valid branch
        Branch validBranch = new Branch("Downtown", new Location(40.7128, -74.0060));
        assertEquals(0, validator.validate(validBranch).size());
    }
}