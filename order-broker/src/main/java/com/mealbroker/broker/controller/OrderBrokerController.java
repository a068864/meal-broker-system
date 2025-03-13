package com.mealbroker.broker.controller;

import com.mealbroker.broker.client.LocationServiceClient;
import com.mealbroker.broker.client.RestaurantServiceClient;
import com.mealbroker.broker.dto.NearbyBranchesRequestDTO;
import com.mealbroker.broker.dto.OrderRequestDTO;
import com.mealbroker.broker.dto.OrderResponseDTO;
import com.mealbroker.broker.dto.OrderStatusUpdateDTO;
import com.mealbroker.broker.service.OrderBrokerService;
import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order broker operations (simplified)
 */
@RestController
@RequestMapping("/api/broker")
public class OrderBrokerController {

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
        OrderResponseDTO response = brokerService.updateOrderStatus(orderId, statusUpdate.getStatus());
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an order
     */
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        OrderResponseDTO response = brokerService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Simple health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Broker Service is up and running!");
    }

    /**
     * Find nearby branches for a restaurant
     */
    @PostMapping("/nearby-branches")
    public ResponseEntity<List<Branch>> findNearbyBranches(
            @RequestParam Long restaurantId,
            @RequestBody Location customerLocation,
            @RequestParam(required = false, defaultValue = "10.0") double maxDistanceKm) {

        // Get all branches of the restaurant
        List<Branch> branches = restaurantServiceClient.getBranchesByRestaurant(restaurantId);
        if (branches.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Use location service to find nearby branches
        NearbyBranchesRequestDTO requestDTO = new NearbyBranchesRequestDTO(
                customerLocation, branches, maxDistanceKm);

        List<Branch> nearbyBranches = locationServiceClient.findNearbyBranches(requestDTO);
        return ResponseEntity.ok(nearbyBranches);
    }
}