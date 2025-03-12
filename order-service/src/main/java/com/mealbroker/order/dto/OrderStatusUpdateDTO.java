package com.mealbroker.order.dto;

import com.mealbroker.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for order status updates
 */
public class OrderStatusUpdateDTO {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    private String notes;

    // Constructors
    public OrderStatusUpdateDTO() {
    }

    public OrderStatusUpdateDTO(OrderStatus status) {
        this.status = status;
    }

    // Getters and Setters
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}