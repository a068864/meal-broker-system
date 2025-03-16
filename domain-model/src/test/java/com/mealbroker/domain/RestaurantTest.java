package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testConstructors() {
        // Default constructor
        Restaurant restaurant1 = new Restaurant();
        assertNull(restaurant1.getRestaurantId());
        assertNull(restaurant1.getName());
        assertNull(restaurant1.getCuisine());

        // Constructor with name and cuisine
        Restaurant restaurant2 = new Restaurant("McDonald's", "Fast Food");
        assertNull(restaurant2.getRestaurantId());
        assertEquals("McDonald's", restaurant2.getName());
        assertEquals("Fast Food", restaurant2.getCuisine());

        // Constructor with ID, name and cuisine
        Restaurant restaurant3 = new Restaurant(1L, "KFC", "Fried Chicken");
        assertEquals(1L, restaurant3.getRestaurantId());
        assertEquals("KFC", restaurant3.getName());
        assertEquals("Fried Chicken", restaurant3.getCuisine());
    }

    @Test
    void testSetters() {
        Restaurant restaurant = new Restaurant();

        restaurant.setRestaurantId(1L);
        restaurant.setName("McDonald's");
        restaurant.setCuisine("Fast Food");

        assertEquals(1L, restaurant.getRestaurantId());
        assertEquals("McDonald's", restaurant.getName());
        assertEquals("Fast Food", restaurant.getCuisine());
    }

    @Test
    void testBranchOperations() {
        Restaurant restaurant = new Restaurant("McDonald's", "Fast Food");

        // Test initial state
        assertTrue(restaurant.getBranches().isEmpty());

        // Create branches
        Branch branch1 = new Branch("Downtown", new Location(43.6532, -79.3832));
        Branch branch2 = new Branch("North York", new Location(43.7615, -79.4111));

        // Test adding branches
        restaurant.addBranch(branch1);
        restaurant.addBranch(branch2);

        assertEquals(2, restaurant.getBranches().size());
        assertTrue(restaurant.getBranches().contains(branch1));
        assertTrue(restaurant.getBranches().contains(branch2));
        assertEquals(restaurant, branch1.getRestaurant());
        assertEquals(restaurant, branch2.getRestaurant());

        // Test removing branches
        restaurant.removeBranch(branch1);

        assertEquals(1, restaurant.getBranches().size());
        assertTrue(restaurant.getBranches().contains(branch2));
        assertNull(branch1.getRestaurant());

        // Test setting branches list
        restaurant.setBranches(null);
        assertEquals(0, restaurant.getBranches().size());
    }

    @Test
    void testGetActiveBranches() {
        Restaurant restaurant = new Restaurant("McDonald's", "Fast Food");

        Branch branch1 = new Branch("Downtown", new Location(43.6532, -79.3832));
        branch1.setActive(true);

        Branch branch2 = new Branch("North York", new Location(43.7615, -79.4111));
        branch2.setActive(false);
        assertFalse(branch2.isActive());

        Branch branch3 = new Branch("Midtown", new Location(43.7046, -79.3980));
        branch3.setActive(true);

        restaurant.addBranch(branch1);
        restaurant.addBranch(branch2);
        restaurant.addBranch(branch3);

        assertEquals(3, restaurant.getBranches().size());
        assertEquals(2, restaurant.getActiveBranches().size());
    }

    @Test
    void testToString() {
        Restaurant restaurant = new Restaurant(1L, "McDonald's", "Fast Food");

        // Add some branches
        Branch branch1 = new Branch("Downtown", new Location(43.6532, -79.3832));
        Branch branch2 = new Branch("North York", new Location(43.7615, -79.4111));
        restaurant.addBranch(branch1);
        restaurant.addBranch(branch2);

        String toString = restaurant.toString();

        assertTrue(toString.contains("McDonald's"));
        assertTrue(toString.contains("Fast Food"));
        assertTrue(toString.contains("branchCount=2"));
    }

    @Test
    void testValidation() {
        // Valid restaurant
        Restaurant validRestaurant = new Restaurant("McDonald's", "Fast Food");
        assertEquals(0, validator.validate(validRestaurant).size());

        // Missing name
        Restaurant missingName = new Restaurant("", "Fast Food");
        assertFalse(validator.validate(missingName).isEmpty());
    }
}