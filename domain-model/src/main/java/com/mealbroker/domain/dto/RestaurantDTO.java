package com.mealbroker.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Restaurant entities
 */
public class RestaurantDTO {

    private Long restaurantId;

    @NotBlank(message = "Restaurant name is required")
    private String name;

    private String cuisine;

    @Valid
    private List<BranchDTO> branches = new ArrayList<>();

    // Constructors
    public RestaurantDTO() {
    }

    public RestaurantDTO(Long restaurantId, String name, String cuisine) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.cuisine = cuisine;
    }

    // Getters and Setters
    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public List<BranchDTO> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchDTO> branches) {
        this.branches = branches;
    }
}