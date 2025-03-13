package com.mealbroker.location.dto;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object for finding the nearest branch
 */
public class NearestBranchRequestDTO {

    @NotNull(message = "Customer location is required")
    private Location customerLocation;

    @NotNull(message = "Branches list is required")
    private List<Branch> branches;

    private boolean activeOnly = true;

    private double maxDistanceKm = 20.0; // Default 20km radius

    // Constructors
    public NearestBranchRequestDTO() {
    }

    public NearestBranchRequestDTO(Location customerLocation, List<Branch> branches) {
        this.customerLocation = customerLocation;
        this.branches = branches;
    }

    public NearestBranchRequestDTO(Location customerLocation, List<Branch> branches, boolean activeOnly) {
        this.customerLocation = customerLocation;
        this.branches = branches;
        this.activeOnly = activeOnly;
    }

    public NearestBranchRequestDTO(Location customerLocation, List<Branch> branches, boolean activeOnly, double maxDistanceKm) {
        this.customerLocation = customerLocation;
        this.branches = branches;
        this.activeOnly = activeOnly;
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

    public boolean isActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(boolean activeOnly) {
        this.activeOnly = activeOnly;
    }

    public double getMaxDistanceKm() {
        return maxDistanceKm;
    }

    public void setMaxDistanceKm(double maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }
}