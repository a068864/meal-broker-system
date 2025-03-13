package com.mealbroker.domain.dto;

import com.mealbroker.domain.Location;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Branch entities
 */
public class BranchDTO {

    private Long branchId;

    @NotBlank(message = "Branch name is required")
    private String branchName;

    private Long restaurantId;

    @NotNull(message = "Location is required")
    @Valid
    private Location location;

    private boolean active = true;

    @Valid
    private MenuDTO menu;

    // Constructors
    public BranchDTO() {
    }

    public BranchDTO(Long branchId, String branchName, Long restaurantId, Location location) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.restaurantId = restaurantId;
        this.location = location;
    }

    // Getters and Setters
    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MenuDTO getMenu() {
        return menu;
    }

    public void setMenu(MenuDTO menu) {
        this.menu = menu;
    }
}