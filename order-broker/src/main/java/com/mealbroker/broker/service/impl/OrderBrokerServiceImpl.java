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
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    @Autowired
    public OrderBrokerServiceImpl(
            OrderServiceClient orderServiceClient,
            RestaurantServiceClient restaurantServiceClient,
            CustomerServiceClient customerServiceClient,
            LocationServiceClient locationServiceClient) {
        this.orderServiceClient = orderServiceClient;
        this.restaurantServiceClient = restaurantServiceClient;
        this.customerServiceClient = customerServiceClient;
        this.locationServiceClient = locationServiceClient;
    }

    /**
     * Place a new order
     *
     * @param orderRequest the order request
     * @return the order response
     */
    @Override
    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequest) {
        try {
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
        } catch (Exception e) {
            logger.error("Error placing order", e);
            OrderResponseDTO errorResponse = new OrderResponseDTO();
            errorResponse.setMessage("Failed to place order: " + e.getMessage());
            return errorResponse;
        }
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
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status) {
        try {
            return orderServiceClient.updateOrderStatus(orderId, status);
        } catch (Exception e) {
            logger.error("Error updating order status", e);
            OrderResponseDTO errorResponse = new OrderResponseDTO();
            errorResponse.setMessage("Failed to update order status: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * Cancel an order
     *
     * @param orderId the order ID
     * @return the cancelled order
     */
    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId) {
        try {
            return orderServiceClient.cancelOrder(orderId);
        } catch (Exception e) {
            logger.error("Error cancelling order", e);
            OrderResponseDTO errorResponse = new OrderResponseDTO();
            errorResponse.setMessage("Failed to cancel order: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * Get order history for an order
     *
     * @param orderId the order ID
     * @return list of order history entries
     */
    @Override
    @Transactional
    public List<OrderHistoryDTO> getOrderHistory(Long orderId) {
        try {
            return orderServiceClient.getOrderHistory(orderId);
        } catch (Exception e) {
            logger.error("Error retrieving order history", e);
            return Collections.emptyList();
        }
    }
}