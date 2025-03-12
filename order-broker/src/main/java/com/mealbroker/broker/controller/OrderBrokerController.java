package com.mealbroker.broker.controller;

import com.mealbroker.broker.dto.OrderRequestDTO;
import com.mealbroker.broker.dto.OrderResponseDTO;
import com.mealbroker.broker.dto.OrderStatusUpdateDTO;
import com.mealbroker.broker.service.OrderBrokerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for order broker operations (simplified)
 */
@RestController
@RequestMapping("/api/broker")
public class OrderBrokerController {

    private final OrderBrokerService brokerService;

    @Autowired
    public OrderBrokerController(OrderBrokerService brokerService) {
        this.brokerService = brokerService;
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
}