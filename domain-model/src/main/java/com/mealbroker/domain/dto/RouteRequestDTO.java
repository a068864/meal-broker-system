package com.mealbroker.domain.dto;

import com.mealbroker.domain.Location;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object for route optimization requests
 */
public class RouteRequestDTO {

    @NotNull(message = "Origin location is required")
    private Location origin;

    @NotEmpty(message = "At least one destination is required")
    private List<Location> destinations;

    // Constructors
    public RouteRequestDTO() {
    }

    public RouteRequestDTO(Location origin, List<Location> destinations) {
        this.origin = origin;
        this.destinations = destinations;
    }

    // Getters and Setters
    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public List<Location> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Location> destinations) {
        this.destinations = destinations;
    }
}