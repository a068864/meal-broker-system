package com.mealbroker.broker.dto;

import com.mealbroker.domain.Location;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Data Transfer Object for nearby location requests
 */
public class NearbyRequestDTO {

    @NotNull(message = "Origin location is required")
    private Location origin;

    @NotNull(message = "Locations list is required")
    private List<Location> locations;

    @NotNull(message = "Radius is required")
    @Positive(message = "Radius must be positive")
    private Double radius;

    /**
     * Flag to determine whether to include the origin location in results
     */
    private boolean includeOrigin = false;

    // Constructors
    public NearbyRequestDTO() {
    }

    public NearbyRequestDTO(Location origin, List<Location> locations, Double radius) {
        this.origin = origin;
        this.locations = locations;
        this.radius = radius;
    }

    public NearbyRequestDTO(Location origin, List<Location> locations, Double radius, boolean includeOrigin) {
        this.origin = origin;
        this.locations = locations;
        this.radius = radius;
        this.includeOrigin = includeOrigin;
    }

    // Getters and Setters
    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public boolean isIncludeOrigin() {
        return includeOrigin;
    }

    public void setIncludeOrigin(boolean includeOrigin) {
        this.includeOrigin = includeOrigin;
    }
}