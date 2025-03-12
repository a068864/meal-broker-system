package com.mealbroker.location.dto;

import com.mealbroker.domain.Location;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for nearby locations requests
 */
public class NearbyRequestDTO {

    @NotNull(message = "Center location is required")
    private Location center;

    @NotEmpty(message = "Locations list cannot be empty")
    private List<Location> locations;

    @Min(value = 0, message = "Radius must be non-negative")
    private double radiusKm;

    // Constructors
    public NearbyRequestDTO() {
    }

    public NearbyRequestDTO(com.mealbroker.domain.Location center, List<com.mealbroker.domain.Location> locations, double radiusKm) {
        this.center = center;
        this.locations = locations;
        this.radiusKm = radiusKm;
    }

    // Getters and Setters
    public com.mealbroker.domain.Location getCenter() {
        return center;
    }

    public void setCenter(com.mealbroker.domain.Location center) {
        this.center = center;
    }

    public List<com.mealbroker.domain.Location> getLocations() {
        return locations;
    }

    public void setLocations(List<com.mealbroker.domain.Location> locations) {
        this.locations = locations;
    }

    public double getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(double radiusKm) {
        this.radiusKm = radiusKm;
    }
}