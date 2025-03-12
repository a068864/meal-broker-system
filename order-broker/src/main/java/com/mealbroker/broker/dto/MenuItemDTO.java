package com.mealbroker.broker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object for menu items in an order
 */
public class MenuItemDTO {

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    private String name;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private Double price;

    private List<String> specialInstructions;

    // Constructors
    public MenuItemDTO() {
    }

    public MenuItemDTO(Long menuItemId, Integer quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(List<String> specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}