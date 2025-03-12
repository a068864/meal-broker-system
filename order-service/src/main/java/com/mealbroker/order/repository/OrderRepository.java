package com.mealbroker.order.repository;

import com.mealbroker.domain.Order;
import com.mealbroker.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for Order entity
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find orders by customer ID
     *
     * @param customerId the customer ID
     * @return list of orders for the customer
     */
    List<Order> findByCustomerCustomerId(Long customerId);

    /**
     * Find orders by customer ID with pagination
     *
     * @param customerId the customer ID
     * @param pageable   pagination information
     * @return page of orders for the customer
     */
    Page<Order> findByCustomerCustomerId(Long customerId, Pageable pageable);

    /**
     * Find orders by restaurant ID
     *
     * @param restaurantId the restaurant ID
     * @return list of orders for the restaurant
     */
    List<Order> findByRestaurantRestaurantId(Long restaurantId);

    /**
     * Find orders by branch ID
     *
     * @param branchId the branch ID
     * @return list of orders for the branch
     */
    List<Order> findByBranchBranchId(Long branchId);

    /**
     * Find orders by status
     *
     * @param status the order status
     * @return list of orders with the given status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by branch ID and status
     *
     * @param branchId the branch ID
     * @param status   the order status
     * @return list of orders for the branch with the given status
     */
    List<Order> findByBranchBranchIdAndStatus(Long branchId, OrderStatus status);

    /**
     * Find orders by customer ID and status
     *
     * @param customerId the customer ID
     * @param status     the order status
     * @return list of orders for the customer with the given status
     */
    List<Order> findByCustomerCustomerIdAndStatus(Long customerId, OrderStatus status);

    /**
     * Find orders by date range
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of orders within the date range
     */
    List<Order> findByOrderTimeBetween(Date startDate, Date endDate);
}
