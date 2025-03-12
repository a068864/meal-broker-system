package com.mealbroker.location.dto;

import com.mealbroker.domain.Location;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for location requests
 */
public class LocationRequestDTO {

    @NotNull(message = "Origin location is required")
    private Location origin;

    @NotNull(message = "Destination location is required")
    private Location destination;

    // Constructors
    public LocationRequestDTO() {
    }

    public LocationRequestDTO(Location origin, Location destination) {
        this.origin = origin;
        this.destination = destination;
    }

    // Getters and Setters
    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }
}