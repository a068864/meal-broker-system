package com.mealbroker.broker.service.impl;

import com.mealbroker.broker.client.CustomerServiceClient;
import com.mealbroker.broker.client.LocationServiceClient;
import com.mealbroker.broker.client.OrderServiceClient;
import com.mealbroker.broker.client.RestaurantServiceClient;
import com.mealbroker.broker.exception.BrokerException;
import com.mealbroker.broker.service.OrderBrokerService;
import com.mealbroker.domain.Branch;
import com.mealbroker.domain.OrderStatus;
import com.mealbroker.domain.dto.*;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the OrderBrokerService interface
 * Acts as the central component in the Broker pattern
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
    @Override
    @Transactional
    @Retry(name = "placeOrder", fallbackMethod = "placeOrderFallback")
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
            NearestBranchRequestDTO nearestBranchRequest = new NearestBranchRequestDTO(
                    orderRequest.getCustomerLocation(), branches, true);
            Branch nearestBranch = locationServiceClient.findNearestBranch(nearestBranchRequest);

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
     * Fallback method for placeOrder
     */
    private OrderResponseDTO placeOrderFallback(OrderRequestDTO orderRequest, Throwable throwable) {
        logger.error("Executing fallback for placeOrder", throwable);
        OrderResponseDTO errorResponse = new OrderResponseDTO();
        errorResponse.setMessage("Service unavailable");
        return errorResponse;
    }

    /**
     * Update order status
     *
     * @param orderId the order ID
     * @param status  the new status
     * @return the updated order
     */
    @Override
    @Transactional
    @Retry(name = "updateOrderStatus", fallbackMethod = "updateOrderStatusFallback")
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
     * Fallback method for updateOrderStatus
     */
    private OrderResponseDTO updateOrderStatusFallback(OrderRequestDTO orderRequest, Throwable throwable) {
        logger.error("Executing fallback for updateOrderStatus", throwable);
        OrderResponseDTO errorResponse = new OrderResponseDTO();
        errorResponse.setMessage("Service unavailable");
        return errorResponse;
    }

    /**
     * Cancel an order
     *
     * @param orderId the order ID
     * @return the cancelled order
     */
    @Override
    @Transactional
    @Retry(name = "cancelOrder", fallbackMethod = "cancelOrderFallback")
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
     * Fallback method for cancelOrder
     */
    private OrderResponseDTO cancelOrderFallback(OrderRequestDTO orderRequest, Throwable throwable) {
        logger.error("Executing fallback for cancelOrder", throwable);
        OrderResponseDTO errorResponse = new OrderResponseDTO();
        errorResponse.setMessage("Service unavailable");
        return errorResponse;
    }

    /**
     * Get order history for an order
     *
     * @param orderId the order ID
     * @return list of order history entries
     */
    @Override
    @Transactional
    @Retry(name = "getOrderHistory", fallbackMethod = "getOrderHistoryFallback")
    public List<OrderHistoryDTO> getOrderHistory(Long orderId) {
        CircuitBreaker getOrderHistoryCB = circuitBreakerFactory.create("getOrderHistory");
        return getOrderHistoryCB.run(() -> orderServiceClient.getOrderHistory(orderId),
                throwable -> {
                    logger.error("Error retrieving order history", throwable);
                    return List.of();
                });
    }

    /**
     * Fallback method for getOrderHistory
     */
    private OrderResponseDTO getOrderHistoryFallback(OrderRequestDTO orderRequest, Throwable throwable) {
        logger.error("Executing fallback for getOrderHistory", throwable);
        OrderResponseDTO errorResponse = new OrderResponseDTO();
        errorResponse.setMessage("Service unavailable");
        return errorResponse;
    }
}