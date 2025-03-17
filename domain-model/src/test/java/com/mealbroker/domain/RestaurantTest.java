package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private Restaurant restaurant;
    private Branch branch1;
    private Branch branch2;
    private Branch branch3;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant("Test Restaurant", "Test Cuisine");

        branch1 = new Branch("Downtown", new Location(43.6532, -79.3832));
        branch2 = new Branch("North York", new Location(43.7615, -79.4111));
        branch3 = new Branch("Midtown", new Location(43.7046, -79.3980));

        branch1.setActive(true);
        branch2.setActive(false);
        branch3.setActive(true);
    }

    @Test
    void testConstructors() {
        Restaurant defaultRestaurant = new Restaurant();
        assertNull(defaultRestaurant.getRestaurantId());
        assertNull(defaultRestaurant.getName());
        assertNull(defaultRestaurant.getCuisine());
        assertNotNull(defaultRestaurant.getBranches());
        assertTrue(defaultRestaurant.getBranches().isEmpty());

        String name = "McDonald's";
        String cuisine = "Fast Food";
        Restaurant nameRestaurant = new Restaurant(name, cuisine);

        assertNull(nameRestaurant.getRestaurantId());
        assertEquals(name, nameRestaurant.getName());
        assertEquals(cuisine, nameRestaurant.getCuisine());
        assertNotNull(nameRestaurant.getBranches());
        assertTrue(nameRestaurant.getBranches().isEmpty());

        Long id = 42L;
        Restaurant restaurant2 = new Restaurant(id, name, cuisine);

        assertEquals(id, restaurant2.getRestaurantId());
        assertEquals(name, restaurant2.getName());
        assertEquals(cuisine, restaurant2.getCuisine());
        assertNotNull(restaurant2.getBranches());
        assertTrue(restaurant2.getBranches().isEmpty());
    }

    @Test
    void testSetRestaurantId() {
        Long id = 42L;
        restaurant.setRestaurantId(id);
        assertEquals(id, restaurant.getRestaurantId());
    }

    @Test
    void testSetName() {
        String name = "Burger King";
        restaurant.setName(name);
        assertEquals(name, restaurant.getName());
    }

    @Test
    void testSetCuisine() {
        String cuisine = "Burgers";
        restaurant.setCuisine(cuisine);
        assertEquals(cuisine, restaurant.getCuisine());
    }

    @Test
    void testAddBranch() {
        restaurant.addBranch(branch1);
        restaurant.addBranch(branch2);

        assertEquals(2, restaurant.getBranches().size());
        assertTrue(restaurant.getBranches().contains(branch1));
        assertTrue(restaurant.getBranches().contains(branch2));
        assertEquals(restaurant, branch1.getRestaurant());
        assertEquals(restaurant, branch2.getRestaurant());

        // Try adding the same branch again (should not duplicate)
        restaurant.addBranch(branch1);
        assertEquals(2, restaurant.getBranches().size());
    }

    @Test
    void testRemoveBranch() {
        restaurant.addBranch(branch1);
        restaurant.addBranch(branch2);

        restaurant.removeBranch(branch1);
        assertEquals(1, restaurant.getBranches().size());
        assertFalse(restaurant.getBranches().contains(branch1));
        assertTrue(restaurant.getBranches().contains(branch2));
        assertNull(branch1.getRestaurant());
        assertEquals(restaurant, branch2.getRestaurant());

        // Try removing a branch that's not in the restaurant
        Branch newBranch = new Branch("New Branch", new Location(45.0, -75.0));
        restaurant.removeBranch(newBranch);
        assertEquals(1, restaurant.getBranches().size());
    }

    @Test
    void testSetBranches() {
        restaurant.addBranch(branch1);

        List<Branch> newBranches = Arrays.asList(branch2, branch3);
        restaurant.setBranches(newBranches);

        assertEquals(2, restaurant.getBranches().size());
        assertFalse(restaurant.getBranches().contains(branch1));
        assertTrue(restaurant.getBranches().contains(branch2));
        assertTrue(restaurant.getBranches().contains(branch3));
        assertNull(branch1.getRestaurant());
        assertEquals(restaurant, branch2.getRestaurant());
        assertEquals(restaurant, branch3.getRestaurant());

        // Test with null
        restaurant.setBranches(null);
        assertEquals(0, restaurant.getBranches().size());
        assertNull(branch2.getRestaurant());
        assertNull(branch3.getRestaurant());
    }

    @Test
    void testGetActiveBranches() {
        restaurant.addBranch(branch1); // active
        restaurant.addBranch(branch2); // inactive
        restaurant.addBranch(branch3); // active

        List<Branch> activeBranches = restaurant.getActiveBranches();
        assertEquals(2, activeBranches.size());
        assertTrue(activeBranches.contains(branch1));
        assertFalse(activeBranches.contains(branch2));
        assertTrue(activeBranches.contains(branch3));
    }

    @Test
    void testAddOrder() {
        Customer customer = new Customer("John Doe", "john@example.com", "+12345678901");
        Order order1 = new Order(customer, restaurant);
        Order order2 = new Order(customer, restaurant);

        restaurant.addOrder(order1);
        restaurant.addOrder(order2);

        assertEquals(2, restaurant.getOrders().size());
        assertTrue(restaurant.getOrders().contains(order1));
        assertTrue(restaurant.getOrders().contains(order2));
        assertEquals(restaurant, order1.getRestaurant());
        assertEquals(restaurant, order2.getRestaurant());

        // Try adding the same order again (should not duplicate)
        restaurant.addOrder(order1);
        assertEquals(2, restaurant.getOrders().size());
    }

    @Test
    void testRemoveOrder() {
        Customer customer = new Customer("John Doe", "john@example.com", "+12345678901");
        Order order1 = new Order(customer, restaurant);
        Order order2 = new Order(customer, restaurant);

        restaurant.addOrder(order1);
        restaurant.addOrder(order2);

        restaurant.removeOrder(order1);
        assertEquals(1, restaurant.getOrders().size());
        assertFalse(restaurant.getOrders().contains(order1));
        assertTrue(restaurant.getOrders().contains(order2));
        assertNull(order1.getRestaurant());
        assertEquals(restaurant, order2.getRestaurant());
    }

    @Test
    void testToString() {
        Restaurant testRestaurant = new Restaurant(1L, "McDonald's", "Fast Food");
        testRestaurant.addBranch(branch1);
        testRestaurant.addBranch(branch2);

        String toString = testRestaurant.toString();
        assertTrue(toString.contains("McDonald's"));
        assertTrue(toString.contains("Fast Food"));
        assertTrue(toString.contains("branchCount=2"));
    }

    @Test
    void testValidRestaurant() {
        Restaurant validRestaurant = new Restaurant("McDonald's", "Fast Food");
        assertEquals(0, validator.validate(validRestaurant).size());
    }

    @Test
    void testInvalidRestaurantEmptyName() {
        Restaurant invalidRestaurant = new Restaurant("", "Fast Food");
        assertFalse(validator.validate(invalidRestaurant).isEmpty());
    }

    @Test
    void testInvalidRestaurantNullName() {
        Restaurant invalidRestaurant = new Restaurant(null, "Fast Food");
        assertFalse(validator.validate(invalidRestaurant).isEmpty());
    }
}