package com.mealbroker.broker.service.impl;

import com.mealbroker.broker.client.CustomerServiceClient;
import com.mealbroker.broker.client.LocationServiceClient;
import com.mealbroker.broker.client.OrderServiceClient;
import com.mealbroker.broker.client.RestaurantServiceClient;
import com.mealbroker.broker.dto.*;
import com.mealbroker.broker.exception.BrokerException;
import com.mealbroker.broker.service.OrderBrokerService;
import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.OrderStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the OrderBrokerService interface
 * Acts as the central component in the Broker pattern
 * Simplified version without Payment and Notification services
 */
@Service
public class OrderBrokerServiceImpl implements OrderBrokerService {
    private static final Logger logger = LoggerFactory.getLogger(OrderBrokerServiceImpl.class);

    private final OrderServiceClient orderServiceClient;
    private final RestaurantServiceClient restaurantServiceClient;
    private final CustomerServiceClient customerServiceClient;
    private final LocationServiceClient locationServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public OrderBrokerServiceImpl(
            OrderServiceClient orderServiceClient,
            RestaurantServiceClient restaurantServiceClient,
            CustomerServiceClient customerServiceClient,
            LocationServiceClient locationServiceClient,
            CircuitBreakerFactory circuitBreakerFactory) {
        this.orderServiceClient = orderServiceClient;
        this.restaurantServiceClient = restaurantServiceClient;
        this.customerServiceClient = customerServiceClient;
        this.locationServiceClient = locationServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    /**
     * Place a new order
     *
     * @param orderRequest the order request
     * @return the order response
     */
    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequest) {
        CircuitBreaker placeOrderCB = circuitBreakerFactory.create("placeOrder");

        return placeOrderCB.run(() -> {
            // Validate customer
            boolean isValid = customerServiceClient.validateCustomer(orderRequest.getCustomerId());
            if (!isValid) {
                throw new BrokerException("Invalid customer ID: " + orderRequest.getCustomerId());
            }

            // Get all branches of the restaurant
            List<Branch> branches = restaurantServiceClient.getBranchesByRestaurant(
                    orderRequest.getRestaurantId());
            if (branches.isEmpty()) {
                throw new BrokerException("No branches found for restaurant: " + orderRequest.getRestaurantId());
            }

            // Find the nearest branch using the location service
            Branch nearestBranch = locationServiceClient.findNearestBranch(
                    orderRequest.getCustomerLocation(), branches);

            if (nearestBranch == null) {
                throw new BrokerException("Could not determine the nearest branch");
            }

            // Check item availability
            boolean itemsAvailable = restaurantServiceClient.checkItemsAvailability(
                    nearestBranch.getBranchId(), orderRequest.getItems());
            if (!itemsAvailable) {
                throw new BrokerException("Some items are not available at the selected branch");
            }

            // Create order creation request for order service
            OrderCreateRequestDTO orderCreateRequest = new OrderCreateRequestDTO(
                    orderRequest.getCustomerId(),
                    orderRequest.getRestaurantId(),
                    nearestBranch.getBranchId(),
                    orderRequest.getItems(),
                    orderRequest.getCustomerLocation());

            // Create the order
            return orderServiceClient.createOrder(orderCreateRequest);
        }, throwable -> {
            logger.error("Error placing order", throwable);
            OrderResponseDTO errorResponse = new OrderResponseDTO();
            errorResponse.setMessage("Failed to place order: " + throwable.getMessage());
            return errorResponse;
        });
    }

    /**
     * Update order status
     *
     * @param orderId the order ID
     * @param status  the new status
     * @return the updated order
     */
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status) {
        CircuitBreaker updateStatusCB = circuitBreakerFactory.create("updateOrderStatus");

        return updateStatusCB.run(() ->
                        orderServiceClient.updateOrderStatus(orderId, status),
                throwable -> {
                    logger.error("Error updating order status", throwable);
                    OrderResponseDTO errorResponse = new OrderResponseDTO();
                    errorResponse.setMessage("Failed to update order status: " + throwable.getMessage());
                    return errorResponse;
                });
    }

    /**
     * Cancel an order
     *
     * @param orderId the order ID
     * @return the cancelled order
     */
    public OrderResponseDTO cancelOrder(Long orderId) {
        CircuitBreaker cancelOrderCB = circuitBreakerFactory.create("cancelOrder");

        return cancelOrderCB.run(() ->
                        orderServiceClient.cancelOrder(orderId),
                throwable -> {
                    logger.error("Error cancelling order", throwable);
                    OrderResponseDTO errorResponse = new OrderResponseDTO();
                    errorResponse.setMessage("Failed to cancel order: " + throwable.getMessage());
                    return errorResponse;
                });
    }

    /**
     * Find the nearest branch to the customer
     *
     * @param branches         all branches of the restaurant
     * @param customerLocation the customer's location
     * @return the nearest branch
     */
    private Branch findNearestBranch(List<Branch> branches, Location customerLocation) {
        if (branches.size() == 1) {
            return branches.get(0);
        }

        // Create a list of branch locations
        List<Location> branchLocations = branches.stream()
                .map(Branch::getLocation)
                .toList();

        // Use location service to find nearby branches
        NearbyRequestDTO nearbyRequest = new NearbyRequestDTO(
                customerLocation,
                branchLocations,
                5000.0  // 5km radius coverage by default
        );

        // Get nearby branches
        List<Location> nearbyLocations = locationServiceClient.findNearbyLocations(nearbyRequest);

        if (nearbyLocations.isEmpty()) {
            // If no branches within the radius, find the closest overall
            Optional<Branch> closestBranch = branches.stream()
                    .min(Comparator.comparingDouble(branch ->
                            calculateDistance(customerLocation, branch.getLocation())));

            return closestBranch.orElse(branches.get(0));
        } else {
            // Find the branch that matches the first nearby location
            Location nearestLocation = nearbyLocations.get(0);
            return branches.stream()
                    .filter(branch -> locationEquals(branch.getLocation(), nearestLocation))
                    .findFirst()
                    .orElse(branches.get(0));
        }
    }

    /**
     * Calculate distance between two locations (simplified)
     */
    private double calculateDistance(Location loc1, Location loc2) {
        return locationServiceClient.calculateDistance(new LocationRequestDTO(loc1, loc2));
    }

    /**
     * Check if two locations are approximately equal
     */
    private boolean locationEquals(Location loc1, Location loc2) {
        final double EPSILON = 0.0001;
        return Math.abs(loc1.getLatitude() - loc2.getLatitude()) < EPSILON &&
                Math.abs(loc1.getLongitude() - loc2.getLongitude()) < EPSILON;
    }
}