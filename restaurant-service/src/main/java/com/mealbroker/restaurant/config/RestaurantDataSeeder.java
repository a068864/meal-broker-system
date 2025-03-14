package com.mealbroker.restaurant.config;

import com.mealbroker.domain.*;
import com.mealbroker.restaurant.repository.BranchRepository;
import com.mealbroker.restaurant.repository.MenuItemRepository;
import com.mealbroker.restaurant.repository.MenuRepository;
import com.mealbroker.restaurant.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration class to seed restaurant data
 */
@Configuration
public class RestaurantDataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantDataSeeder.class);

    private final RestaurantRepository restaurantRepository;
    private final BranchRepository branchRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public RestaurantDataSeeder(
            RestaurantRepository restaurantRepository,
            BranchRepository branchRepository,
            MenuRepository menuRepository,
            MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.branchRepository = branchRepository;
        this.menuRepository = menuRepository;
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Seeds the database with initial restaurant data
     * Only runs in development and test profiles
     */
    @Bean
    @Profile({"dev", "test"})
    public CommandLineRunner seedRestaurantData() {
        return args -> {
            logger.info("Starting restaurant data seeding...");

            // Check if data already exists
            if (restaurantRepository.count() > 0) {
                logger.info("Restaurant database already seeded, skipping...");
                return;
            }

            // Create restaurants
            createBurgerChain();
            createPizzaChain();
            createSushiChain();

            logger.info("Restaurant data seeding completed.");
        };
    }

    private void createBurgerChain() {
        // Create burger restaurant chain
        Restaurant burgerChain = new Restaurant("Burger Palace", "Fast Food");
        restaurantRepository.save(burgerChain);

        // Create branches in different cities
        Branch torontoBranch = createBranch(burgerChain, "Burger Palace Toronto", new Location(43.6545, -79.3856));
        Branch vancouverBranch = createBranch(burgerChain, "Burger Palace Vancouver", new Location(49.2819, -123.1187));
        Branch montrealBranch = createBranch(burgerChain, "Burger Palace Montreal", new Location(45.5005, -73.5690));

        // Create menus
        createBurgerMenu(torontoBranch);
        createBurgerMenu(vancouverBranch);
        createBurgerMenu(montrealBranch);
    }

    private void createPizzaChain() {
        // Create pizza restaurant chain
        Restaurant pizzaChain = new Restaurant("Pizza Haven", "Italian");
        restaurantRepository.save(pizzaChain);

        // Create branches in different cities
        Branch torontoBranch = createBranch(pizzaChain, "Pizza Haven Toronto", new Location(43.6510, -79.3822));
        Branch vancouverBranch = createBranch(pizzaChain, "Pizza Haven Vancouver", new Location(49.2845, -123.1226));
        Branch calgaryBranch = createBranch(pizzaChain, "Pizza Haven Calgary", new Location(51.0432, -114.0692));

        // Create menus
        createPizzaMenu(torontoBranch);
        createPizzaMenu(vancouverBranch);
        createPizzaMenu(calgaryBranch);
    }

    private void createSushiChain() {
        // Create sushi restaurant chain
        Restaurant sushiChain = new Restaurant("Sushi Express", "Japanese");
        restaurantRepository.save(sushiChain);

        // Create branches in different cities
        Branch torontoBranch = createBranch(sushiChain, "Sushi Express Toronto", new Location(43.6578, -79.3884));
        Branch vancouverBranch = createBranch(sushiChain, "Sushi Express Vancouver", new Location(49.2863, -123.1245));
        Branch ottawaBranch = createBranch(sushiChain, "Sushi Express Ottawa", new Location(45.4231, -75.6954));

        // Create menus
        createSushiMenu(torontoBranch);
        createSushiMenu(vancouverBranch);
        createSushiMenu(ottawaBranch);
    }

    private Branch createBranch(Restaurant restaurant, String branchName, Location location) {
        Branch branch = new Branch(branchName, location);
        branch.setActive(true);
        branch.setRestaurant(restaurant);
        return branchRepository.save(branch);
    }

    private void createBurgerMenu(Branch branch) {
        Menu menu = new Menu();
        menu.setBranch(branch);
        menuRepository.save(menu);
        branch.setMenu(menu);
        branchRepository.save(branch);

        // Create menu items
        createMenuItem(menu, "Classic Burger", "Beef patty with lettuce, tomato, and special sauce", 8.99, Arrays.asList("Gluten", "Dairy"));
        createMenuItem(menu, "Cheeseburger", "Classic burger with cheddar cheese", 9.99, Arrays.asList("Gluten", "Dairy"));
        createMenuItem(menu, "Bacon Burger", "Classic burger with crispy bacon", 10.99, Arrays.asList("Gluten", "Dairy"));
        createMenuItem(menu, "Veggie Burger", "Plant-based patty with all the fixings", 9.99, Arrays.asList("Gluten"));
        createMenuItem(menu, "French Fries", "Crispy golden fries", 3.99, Arrays.asList("Gluten"));
        createMenuItem(menu, "Onion Rings", "Battered and fried onion rings", 4.99, Arrays.asList("Gluten", "Dairy"));
        createMenuItem(menu, "Soda", "Assorted soft drinks", 1.99, new ArrayList<>());
    }

    private void createPizzaMenu(Branch branch) {
        Menu menu = new Menu();
        menu.setBranch(branch);
        menuRepository.save(menu);
        branch.setMenu(menu);
        branchRepository.save(branch);

        // Create menu items
        createMenuItem(menu, "Margherita Pizza", "Classic pizza with tomato sauce, mozzarella, and basil", 12.99, Arrays.asList("Gluten", "Dairy"));
        createMenuItem(menu, "Pepperoni Pizza", "Pizza with tomato sauce, mozzarella, and pepperoni", 14.99, Arrays.asList("Gluten", "Dairy"));
        createMenuItem(menu, "Vegetarian Pizza", "Pizza with tomato sauce, mozzarella, and assorted vegetables", 13.99, Arrays.asList("Gluten", "Dairy"));
        createMenuItem(menu, "Hawaiian Pizza", "Pizza with tomato sauce, mozzarella, ham, and pineapple", 15.99, Arrays.asList("Gluten", "Dairy"));
        createMenuItem(menu, "Garlic Bread", "Toasted bread with garlic butter", 4.99, Arrays.asList("Gluten", "Dairy"));
        createMenuItem(menu, "Caesar Salad", "Romaine lettuce with Caesar dressing, croutons, and parmesan", 6.99, Arrays.asList("Gluten", "Dairy", "Eggs"));
        createMenuItem(menu, "Soda", "Assorted soft drinks", 1.99, new ArrayList<>());
    }

    private void createSushiMenu(Branch branch) {
        Menu menu = new Menu();
        menu.setBranch(branch);
        menuRepository.save(menu);
        branch.setMenu(menu);
        branchRepository.save(branch);

        // Create menu items
        createMenuItem(menu, "California Roll", "Crab, avocado, and cucumber roll", 7.99, Arrays.asList("Shellfish", "Eggs"));
        createMenuItem(menu, "Salmon Nigiri", "Fresh salmon on rice", 9.99, Arrays.asList("Fish"));
        createMenuItem(menu, "Tuna Nigiri", "Fresh tuna on rice", 10.99, Arrays.asList("Fish"));
        createMenuItem(menu, "Tempura Roll", "Tempura shrimp, avocado, and cucumber roll", 12.99, Arrays.asList("Shellfish", "Gluten"));
        createMenuItem(menu, "Vegetable Roll", "Assorted vegetables roll", 6.99, new ArrayList<>());
        createMenuItem(menu, "Miso Soup", "Traditional Japanese soup with tofu and seaweed", 3.99, Arrays.asList("Soy"));
        createMenuItem(menu, "Green Tea", "Traditional Japanese green tea", 2.99, new ArrayList<>());
    }

    private MenuItem createMenuItem(Menu menu, String name, String description, double price, List<String> allergens) {
        MenuItem menuItem = new MenuItem(name, description, price);
        menuItem.setAvailable(true);
        menuItem.setStock(100);
        menuItem.setMenu(menu);

        // Add allergens
        for (String allergen : allergens) {
            menuItem.addAllergen(allergen);
        }

        return menuItemRepository.save(menuItem);
    }
}