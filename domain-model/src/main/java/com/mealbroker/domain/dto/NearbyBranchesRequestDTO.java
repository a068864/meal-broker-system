package com.mealbroker.domain.dto;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Data Transfer Object for nearby branches requests
 */
public class NearbyBranchesRequestDTO {

    @NotNull(message = "Customer location is required")
    private Location customerLocation;

    @NotNull(message = "Branches list is required")
    private List<Branch> branches;

    @Positive(message = "Maximum distance must be positive")
    private double maxDistanceKm = 10.0; // Default 10km radius

    // Constructors
    public NearbyBranchesRequestDTO() {
    }

    public NearbyBranchesRequestDTO(Location customerLocation, List<Branch> branches, double maxDistanceKm) {
        this.customerLocation = customerLocation;
        this.branches = branches;
        this.maxDistanceKm = maxDistanceKm;
    }

    // Getters and Setters
    public Location getCustomerLocation() {
        return customerLocation;
    }

    public void setCustomerLocation(Location customerLocation) {
        this.customerLocation = customerLocation;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public double getMaxDistanceKm() {
        return maxDistanceKm;
    }

    public void setMaxDistanceKm(double maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }
}