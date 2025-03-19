package com.mealbroker.broker.service;

import com.mealbroker.domain.OrderStatus;
import com.mealbroker.domain.dto.OrderHistoryDTO;
import com.mealbroker.domain.dto.OrderRequestDTO;
import com.mealbroker.domain.dto.OrderResponseDTO;

import java.util.List;

/**
 * Service interface for order broker operations
 */
public interface OrderBrokerService {

    /**
     * Place a new order
     *
     * @param orderRequest the order request data
     * @return order response with details
     */
    OrderResponseDTO placeOrder(OrderRequestDTO orderRequest);

    /**
     * Update order status
     *
     * @param orderId the order ID
     * @param status  the new status
     * @return order response with updated details
     */
    OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status);

    /**
     * Cancel an order
     *
     * @param orderId the order ID
     * @return order response with cancelled details
     */
    OrderResponseDTO cancelOrder(Long orderId);

    /**
     * Get order history for an order
     *
     * @param orderId the orderID
     * @return list of order history entries
     */
    List<OrderHistoryDTO> getOrderHistory(Long orderId);
}