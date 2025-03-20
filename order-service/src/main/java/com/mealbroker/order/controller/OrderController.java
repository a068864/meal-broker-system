package com.mealbroker.order.controller;

import com.mealbroker.domain.OrderStatus;
import com.mealbroker.domain.dto.OrderDTO;
import com.mealbroker.domain.dto.OrderHistoryDTO;
import com.mealbroker.domain.dto.OrderStatusUpdateDTO;
import com.mealbroker.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * REST controller for order operations
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        OrderDTO order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(@PathVariable Long customerId) {
        List<OrderDTO> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByRestaurant(@PathVariable Long restaurantId) {
        List<OrderDTO> orders = orderService.getOrdersByRestaurant(restaurantId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByBranch(@PathVariable Long branchId) {
        List<OrderDTO> orders = orderService.getOrdersByBranch(branchId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(
            @PathVariable OrderStatus status) {
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/branch/{branchId}/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByBranchAndStatus(
            @PathVariable Long branchId,
            @PathVariable OrderStatus status) {
        List<OrderDTO> orders = orderService.getOrdersByBranchAndStatus(branchId, status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerAndStatus(
            @PathVariable Long customerId,
            @PathVariable OrderStatus status) {
        List<OrderDTO> orders = orderService.getOrdersByCustomerAndStatus(customerId, status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<OrderDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<OrderDTO> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateDTO statusUpdate) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, statusUpdate.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        OrderDTO cancelledOrder = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(cancelledOrder);
    }

    @GetMapping("{orderId}/history")
    public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory(@PathVariable Long orderId) {
        List<OrderHistoryDTO> histories = orderService.getOrderHistory(orderId);
        return ResponseEntity.ok(histories);
    }
}