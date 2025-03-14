package com.mealbroker.order.config;

import com.mealbroker.domain.*;
import com.mealbroker.order.repository.OrderItemRepository;
import com.mealbroker.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Configuration class to seed order data
 * Note: This assumes that customer and restaurant data has already been seeded
 */
@Configuration
public class OrderDataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(OrderDataSeeder.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final Random random = new Random();

    @Autowired
    public OrderDataSeeder(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Seeds the database with initial order data
     * Only runs in development and test profiles
     */
    @Bean
    @Profile({"dev", "test"})
    public CommandLineRunner seedOrderData() {
        return args -> {
            logger.info("Starting order data seeding...");

            // Check if data already exists
            if (orderRepository.count() > 0) {
                logger.info("Order database already seeded, skipping...");
                return;
            }

            // Create sample orders (in a real scenario, these IDs would come from actual customers/restaurants)
            List<Order> orders = new ArrayList<>();

            // Sample customers (IDs should match seeded customer data)
            Long[] customerIds = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L};

            // Sample restaurants and branches (IDs should match seeded restaurant data)
            Long[][] restaurantBranches = {
                    {1L, 1L}, // Burger Palace Toronto
                    {1L, 2L}, // Burger Palace Vancouver
                    {1L, 3L}, // Burger Palace Montreal
                    {2L, 4L}, // Pizza Haven Toronto
                    {2L, 5L}, // Pizza Haven Vancouver
                    {2L, 6L}, // Pizza Haven Calgary
                    {3L, 7L}, // Sushi Express Toronto
                    {3L, 8L}, // Sushi Express Vancouver
                    {3L, 9L}  // Sushi Express Ottawa
            };

            // Create 20 random orders
            for (int i = 0; i < 20; i++) {
                // Select random customer and restaurant/branch
                Long customerId = customerIds[random.nextInt(customerIds.length)];
                Long[] restaurantBranch = restaurantBranches[random.nextInt(restaurantBranches.length)];
                Long restaurantId = restaurantBranch[0];
                Long branchId = restaurantBranch[1];

                // Create customer reference
                Customer customer = new Customer();
                customer.setCustomerId(customerId);

                // Create restaurant reference
                Restaurant restaurant = new Restaurant();
                restaurant.setRestaurantId(restaurantId);

                // Create branch reference
                Branch branch = new Branch();
                branch.setBranchId(branchId);

                // Create order
                Order order = new Order(customer, restaurant);
                order.setBranch(branch);

                // Set a random order time in the past 30 days
                long now = System.currentTimeMillis();
                long thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000);
                long randomTime = thirtyDaysAgo + random.nextInt((int) (now - thirtyDaysAgo));
                order.setOrderTime(new Date(randomTime));

                // Set a random order status
                OrderStatus[] statuses = OrderStatus.values();
                OrderStatus randomStatus = statuses[random.nextInt(statuses.length)];
                order.setStatus(randomStatus);

                // Save the order first to get an ID
                Order savedOrder = orderRepository.save(order);

                // Add 1-4 order items
                int numItems = 1 + random.nextInt(4);
                for (int j = 0; j < numItems; j++) {
                    // Menu item IDs will depend on the restaurant and branch
                    // In reality, these would come from actual menu items
                    // This is just a simulation
                    Long menuItemId = (long) (1 + random.nextInt(7)); // Assuming each menu has 7 items
                    int quantity = 1 + random.nextInt(3);
                    double price = 5.0 + (15.0 * random.nextDouble()); // Random price between $5-$20

                    OrderItem item = new OrderItem(
                            menuItemId,
                            "Sample Item " + menuItemId,
                            quantity,
                            price
                    );

                    // Add a special instruction to some items
                    if (random.nextBoolean()) {
                        item.addSpecialInstruction("Please make it " +
                                (random.nextBoolean() ? "spicy" : "mild"));
                    }

                    item.setOrder(savedOrder);
                    orderItemRepository.save(item);
                }

                orders.add(savedOrder);
            }

            logger.info("Order data seeding completed. Added {} orders.", orders.size());
        };
    }
}