package com.mealbroker.broker.client;

import com.mealbroker.domain.OrderStatus;
import com.mealbroker.domain.dto.OrderCreateRequestDTO;
import com.mealbroker.domain.dto.OrderHistoryDTO;
import com.mealbroker.domain.dto.OrderResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign client for the Order Service
 */
@FeignClient(name = "order-service")
public interface OrderServiceClient {

    /**
     * Create a new order
     *
     * @param orderCreateRequest the order request containing customer, restaurant, branch,
     *                           items, and location information
     * @return the created order
     */
    @PostMapping("/api/orders/create")
    OrderResponseDTO createOrder(@RequestBody OrderCreateRequestDTO orderCreateRequest);

    /**
     * Get an order by ID
     *
     * @param orderId the order ID
     * @return the order if found
     */
    @GetMapping("/api/orders/{orderId}")
    OrderResponseDTO getOrder(@PathVariable Long orderId);

    /**
     * Update order status
     *
     * @param orderId the order ID
     * @param status  the new status
     * @return the updated order
     */
    @PutMapping("/api/orders/{orderId}/status")
    OrderResponseDTO updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatus status);

    /**
     * Cancel an order
     *
     * @param orderId the order ID
     * @return the cancelled order
     */
    @PostMapping("/api/orders/{orderId}/cancel")
    OrderResponseDTO cancelOrder(@PathVariable Long orderId);

    /**
     * Get order history for an order
     *
     * @param orderId the orderID
     * @return list of order history entries
     */
    @GetMapping("/api/orders/{orderId}/history")
    List<OrderHistoryDTO> getOrderHistory(@PathVariable Long orderId);
}