package com.mealbroker.order.service;

import com.mealbroker.domain.Location;
import com.mealbroker.domain.OrderStatus;
import com.mealbroker.domain.dto.OrderDTO;
import com.mealbroker.domain.dto.OrderHistoryDTO;
import com.mealbroker.domain.dto.OrderItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Service interface for order operations
 */
public interface OrderService {

    /**
     * Create a new order
     *
     * @param orderDTO the order data
     * @return the created order
     */
    OrderDTO createOrder(OrderDTO orderDTO);

    @Transactional
    OrderDTO createOrder(Long customerId, Long restaurantId, Long branchId,
                         List<OrderItemDTO> items, Location customerLocation);

    /**
     * Get an order by ID
     *
     * @param orderId the order ID
     * @return the order if found
     */
    OrderDTO getOrder(Long orderId);

    /**
     * Get orders by customer ID
     *
     * @param customerId the customer ID
     * @return list of orders for the customer
     */
    List<OrderDTO> getOrdersByCustomer(Long customerId);

    /**
     * Get orders by customer ID with pagination
     *
     * @param customerId the customer ID
     * @param pageable   pagination information
     * @return page of orders for the customer
     */
    Page<OrderDTO> getOrdersByCustomer(Long customerId, Pageable pageable);

    /**
     * Get orders by restaurant ID
     *
     * @param restaurantId the restaurant ID
     * @return list of orders for the restaurant
     */
    List<OrderDTO> getOrdersByRestaurant(Long restaurantId);

    /**
     * Get orders by branch ID
     *
     * @param branchId the branch ID
     * @return list of orders for the branch
     */
    List<OrderDTO> getOrdersByBranch(Long branchId);

    /**
     * Get orders by status
     *
     * @param status the order status
     * @return list of orders with the given status
     */
    List<OrderDTO> getOrdersByStatus(OrderStatus status);

    /**
     * Get orders by branch ID and status
     *
     * @param branchId the branch ID
     * @param status   the order status
     * @return list of orders for the branch with the given status
     */
    List<OrderDTO> getOrdersByBranchAndStatus(Long branchId, OrderStatus status);

    /**
     * Get orders by customer ID and status
     *
     * @param customerId the customer ID
     * @param status     the order status
     * @return list of orders for the customer with the given status
     */
    List<OrderDTO> getOrdersByCustomerAndStatus(Long customerId, OrderStatus status);

    /**
     * Get orders by date range
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of orders within the date range
     */
    List<OrderDTO> getOrdersByDateRange(Date startDate, Date endDate);

    /**
     * Update order status
     *
     * @param orderId the order ID
     * @param status  the new status
     * @return the updated order
     */
    OrderDTO updateOrderStatus(Long orderId, OrderStatus status);

    /**
     * Cancel an order
     *
     * @param orderId the order ID
     * @return the cancelled order
     */
    OrderDTO cancelOrder(Long orderId);

    /**
     * Get order history for an order
     *
     * @param orderId the order ID
     * @return list of order history entries
     */
    List<OrderHistoryDTO> getOrderHistory(Long orderId);
}