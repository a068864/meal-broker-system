package com.mealbroker.order.repository;

import com.mealbroker.domain.Order;
import com.mealbroker.domain.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderHistory entity
 */
@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    /**
     * Find order history entries by order
     *
     * @param order the order
     * @return list of order history entries
     */
    List<OrderHistory> findByOrder(Order order);

    /**
     * Find order history entries by order ID
     *
     * @param orderId the order ID
     * @return list of order history entries
     */
    List<OrderHistory> findByOrderOrderId(Long orderId);
}
