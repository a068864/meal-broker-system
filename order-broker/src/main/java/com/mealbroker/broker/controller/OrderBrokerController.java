package com.mealbroker.broker.controller;

import com.mealbroker.broker.client.LocationServiceClient;
import com.mealbroker.broker.client.RestaurantServiceClient;
import com.mealbroker.broker.service.OrderBrokerService;
import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order broker operations
 */
@RestController
@RequestMapping("/api/broker")
public class OrderBrokerController {

    private static final Logger logger = LoggerFactory.getLogger(OrderBrokerController.class);

    private final OrderBrokerService brokerService;
    private final RestaurantServiceClient restaurantServiceClient;
    private final LocationServiceClient locationServiceClient;

    @Autowired
    public OrderBrokerController(
            OrderBrokerService brokerService,
            RestaurantServiceClient restaurantServiceClient,
            LocationServiceClient locationServiceClient) {
        this.brokerService = brokerService;
        this.restaurantServiceClient = restaurantServiceClient;
        this.locationServiceClient = locationServiceClient;
    }

    /**
     * Place a new order
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        logger.info("Received order request for customer ID: {} and restaurant ID: {}",
                orderRequest.getCustomerId(), orderRequest.getRestaurantId());
        OrderResponseDTO response = brokerService.placeOrder(orderRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Update order status
     */
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateDTO statusUpdate) {
        logger.info("Updating order status: Order ID {} to status {}", orderId, statusUpdate.getStatus());
        OrderResponseDTO response = brokerService.updateOrderStatus(orderId, statusUpdate.getStatus());
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an order
     */
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        logger.info("Cancelling order: Order ID {}", orderId);
        OrderResponseDTO response = brokerService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Find nearby branches for a restaurant
     */
    @PostMapping("/nearby-branches")
    public ResponseEntity<List<Branch>> findNearbyBranches(
            @RequestParam Long restaurantId,
            @RequestBody Location customerLocation,
            @RequestParam(required = false, defaultValue = "10.0") double maxDistanceKm) {

        logger.info("Finding nearby branches for restaurant ID: {} within {} km", restaurantId, maxDistanceKm);

        // Get all branches of the restaurant
        List<Branch> branches = restaurantServiceClient.getBranchesByRestaurant(restaurantId);
        if (branches.isEmpty()) {
            logger.warn("No branches found for restaurant ID: {}", restaurantId);
            return ResponseEntity.noContent().build();
        }

        // Use location service to find nearby branches
        NearbyBranchesRequestDTO requestDTO = new NearbyBranchesRequestDTO(
                customerLocation, branches, maxDistanceKm);

        List<Branch> nearbyBranches = locationServiceClient.findNearbyBranches(requestDTO);

        if (nearbyBranches.isEmpty()) {
            logger.info("No nearby branches found within {} km for restaurant ID: {}", maxDistanceKm, restaurantId);
            return ResponseEntity.noContent().build();
        }

        logger.info("Found {} nearby branches for restaurant ID: {}", nearbyBranches.size(), restaurantId);
        return ResponseEntity.ok(nearbyBranches);
    }

    /**
     * Find the nearest branch for a restaurant
     */
    @PostMapping("/nearest-branch")
    public ResponseEntity<Branch> findNearestBranch(
            @RequestParam Long restaurantId,
            @RequestBody Location customerLocation) {

        logger.info("Finding nearest branch for restaurant ID: {}", restaurantId);

        // Get all branches of the restaurant
        List<Branch> branches = restaurantServiceClient.getBranchesByRestaurant(restaurantId);
        if (branches.isEmpty()) {
            logger.warn("No branches found for restaurant ID: {}", restaurantId);
            return ResponseEntity.noContent().build();
        }

        // Use location service to find the nearest branch
        NearestBranchRequestDTO requestDTO = new NearestBranchRequestDTO(customerLocation, branches, true);
        Branch nearestBranch = locationServiceClient.findNearestBranch(requestDTO);

        if (nearestBranch == null) {
            logger.info("No suitable branch found for restaurant ID: {}", restaurantId);
            return ResponseEntity.noContent().build();
        }

        logger.info("Found nearest branch ID: {} for restaurant ID: {}", nearestBranch.getBranchId(), restaurantId);
        return ResponseEntity.ok(nearestBranch);
    }

    /**
     * Simple health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Broker Service is up and running!");
    }
}