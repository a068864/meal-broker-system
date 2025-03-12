package com.mealbroker.order.repository;

import com.mealbroker.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderItem entity
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find order items by order ID
     *
     * @param orderId the order ID
     * @return list of order items for the order
     */
    List<OrderItem> findByOrderOrderId(Long orderId);

    /**
     * Find order items by menu item ID
     *
     * @param menuItemId the menu item ID
     * @return list of order items for the menu item
     */
    List<OrderItem> findByMenuItemId(Long menuItemId);
}