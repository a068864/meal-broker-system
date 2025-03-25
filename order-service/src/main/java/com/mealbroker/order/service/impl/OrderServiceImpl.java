package com.mealbroker.order.service.impl;

import com.mealbroker.domain.*;
import com.mealbroker.domain.dto.OrderDTO;
import com.mealbroker.domain.dto.OrderHistoryDTO;
import com.mealbroker.domain.dto.OrderItemDTO;
import com.mealbroker.order.exception.OrderNotFoundException;
import com.mealbroker.order.exception.OrderStatusException;
import com.mealbroker.order.repository.OrderHistoryRepository;
import com.mealbroker.order.repository.OrderRepository;
import com.mealbroker.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the OrderService interface
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderHistoryRepository orderHistoryRepository) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // Create customer, restaurant, and branch references
        Customer customer = new Customer();
        customer.setCustomerId(orderDTO.getCustomerId());

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(orderDTO.getRestaurantId());

        Branch branch = null;
        if (orderDTO.getBranchId() != null) {
            branch = new Branch();
            branch.setBranchId(orderDTO.getBranchId());
        }

        // Create new order
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setBranch(branch);
        order.setOrderTime(orderDTO.getOrderTime() != null ? orderDTO.getOrderTime() : new Date());
        order.setCustomerLocation(orderDTO.getCustomerLocation());

        // Create and save order items directly on the order
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            OrderItem item = new OrderItem(
                    itemDTO.getMenuItemId(),
                    itemDTO.getMenuItemName(),
                    itemDTO.getQuantity(),
                    itemDTO.getPrice()
            );

            if (itemDTO.getAdditionalCharges() != null) {
                item.setAdditionalCharges(itemDTO.getAdditionalCharges());
            }

            if (itemDTO.getSpecialInstructions() != null && !itemDTO.getSpecialInstructions().isEmpty()) {
                for (String instruction : itemDTO.getSpecialInstructions()) {
                    item.addSpecialInstruction(instruction);
                }
            }

            order.addItem(item);
        }

        // Update order with items
        Order savedOrder = orderRepository.save(order);

        // Create initial history entry for order creation
        OrderHistory initialHistory = new OrderHistory(
                savedOrder,
                null,
                OrderStatus.NEW,
                "Order created"
        );
        orderHistoryRepository.save(initialHistory);

        // Convert to DTO and return
        return convertToDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long customerId, Long restaurantId, Long branchId,
                                List<OrderItemDTO> items, Location customerLocation) {
        OrderDTO orderDTO = new OrderDTO(customerId, restaurantId);
        orderDTO.setBranchId(branchId);
        orderDTO.setItems(items);
        orderDTO.setCustomerLocation(customerLocation);

        return createOrder(orderDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        return convertToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerCustomerId(customerId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByCustomer(Long customerId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByCustomerCustomerId(customerId, pageable);
        return orderPage.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByRestaurant(Long restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantRestaurantId(restaurantId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByBranch(Long branchId) {
        List<Order> orders = orderRepository.findByBranchBranchId(branchId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByBranchAndStatus(Long branchId, OrderStatus status) {
        List<Order> orders = orderRepository.findByBranchBranchIdAndStatus(branchId, status);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByCustomerAndStatus(Long customerId, OrderStatus status) {
        List<Order> orders = orderRepository.findByCustomerCustomerIdAndStatus(customerId, status);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByDateRange(Date startDate, Date endDate) {
        List<Order> orders = orderRepository.findByOrderTimeBetween(startDate, endDate);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        OrderStatus previousStatus = order.getStatus();

        // Validate status transition
        validateStatusTransition(previousStatus, newStatus);

        // Update status
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        // Create appropriate notes based on status change
        String notes = generateStatusChangeNotes(previousStatus, newStatus);

        // Create and save history record
        OrderHistory history = new OrderHistory(updatedOrder, previousStatus, newStatus, notes);
        orderHistoryRepository.save(history);

        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        OrderStatus previousStatus = order.getStatus();

        // Check if the order can be cancelled
        if (previousStatus == OrderStatus.COMPLETED) {
            throw new OrderStatusException("Cannot cancel a completed order");
        }

        // Update status to CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        // Create and save history record
        OrderHistory history = new OrderHistory(cancelledOrder, previousStatus, OrderStatus.CANCELLED, "Order cancelled");
        orderHistoryRepository.save(history);

        return convertToDTO(cancelledOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderHistoryDTO> getOrderHistory(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found with ID: " + orderId);
        }

        List<OrderHistory> histories = orderHistoryRepository.findByOrderOrderId(orderId);

        // Convert to DTOs
        return histories.stream().map(this::convertToHistoryDTO).collect(Collectors.toList());
    }

    /**
     * Helper method to generate appropriate notes for status changes
     */
    private String generateStatusChangeNotes(OrderStatus previousStatus, OrderStatus newStatus) {
        if (previousStatus == null) {
            return "Order created with status " + newStatus;
        }
        switch (newStatus) {
            case PROCESSING:
                return "Order processing started";
            case CONFIRMED:
                return "Order confirmed by restaurant";
            case IN_PREPARATION:
                return "Order is being prepared in the kitchen";
            case READY:
                return "Order is ready for pickup/delivery";
            case COMPLETED:
                return "Order has been delivered/picked up";
            case CANCELLED:
                return "Order has been cancelled";
            default:
                return "Status changed from " + previousStatus + " to " + newStatus;
        }
    }

    /**
     * Helper method to validate order status transitions
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        Order tempOrder = new Order();
        tempOrder.setStatus(currentStatus);

        try {
            tempOrder.setStatus(newStatus);
        } catch (IllegalStateException e) {
            throw new OrderStatusException(e.getMessage());
        }
    }

    /**
     * Helper method to convert Order entity to OrderDTO
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setCustomerId(order.getCustomer().getCustomerId());
        orderDTO.setRestaurantId(order.getRestaurant().getRestaurantId());

        if (order.getBranch() != null) {
            orderDTO.setBranchId(order.getBranch().getBranchId());
        }

        orderDTO.setOrderTime(order.getOrderTime());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCustomerLocation(order.getCustomerLocation());

        // Convert order items
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        orderDTO.setItems(itemDTOs);

        // Calculate total amount
        double totalAmount = order.calculateTotal();
        orderDTO.setTotalAmount(totalAmount);

        return orderDTO;
    }

    /**
     * Helper method to convert OrderItem entity to OrderItemDTO
     */
    private OrderItemDTO convertToDTO(OrderItem orderItem) {
        OrderItemDTO itemDTO = new OrderItemDTO(
                orderItem.getMenuItemId(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );

        itemDTO.setOrderItemId(orderItem.getOrderItemId());
        itemDTO.setMenuItemName(orderItem.getMenuItemName());
        itemDTO.setAdditionalCharges(orderItem.getAdditionalCharges());
        itemDTO.setSpecialInstructions(orderItem.getSpecialInstructions());

        return itemDTO;
    }

    /**
     * Helper method to convert OrderHistory to OrderHistoryDTO
     */
    private OrderHistoryDTO convertToHistoryDTO(OrderHistory history) {
        return new OrderHistoryDTO(
                history.getHistoryId(),
                history.getOrder().getOrderId(),
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getTimestamp(),
                history.getNotes()
        );
    }
}