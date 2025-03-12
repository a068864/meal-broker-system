package com.mealbroker.broker.dto;

import com.mealbroker.domain.Location;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for order creation requests sent to the Order Service
 */
public class OrderCreateRequestDTO {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<MenuItemDTO> items = new ArrayList<>();

    @NotNull(message = "Customer location is required")
    private Location customerLocation;

    // Constructors
    public OrderCreateRequestDTO() {
    }

    public OrderCreateRequestDTO(Long customerId, Long restaurantId, Long branchId,
                                 List<MenuItemDTO> items, Location customerLocation) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.branchId = branchId;
        this.items = items;
        this.customerLocation = customerLocation;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public List<MenuItemDTO> getItems() {
        return items;
    }

    public void setItems(List<MenuItemDTO> items) {
        this.items = items;
    }

    public Location getCustomerLocation() {
        return customerLocation;
    }

    public void setCustomerLocation(Location customerLocation) {
        this.customerLocation = customerLocation;
    }
}