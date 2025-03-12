package com.mealbroker.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * Domain object representing a geographical location with latitude and longitude
 */
@Embeddable
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int EARTH_RADIUS_KM = 6371; // Earth radius in kilometers

    @NotNull(message = "Latitude is required")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    private Double longitude;

    /**
     * Default constructor required by JPA
     */
    public Location() {
    }

    /**
     * Create a new location with specified coordinates
     *
     * @param latitude  the latitude coordinate
     * @param longitude the longitude coordinate
     */
    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Calculate distance to another location using the Haversine formula
     *
     * @param other the other location
     * @return distance in kilometers
     */
    public double distanceTo(Location other) {
        if (other == null) {
            return Double.MAX_VALUE;
        }

        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Check if this location is within a specified radius of another location
     *
     * @param other    the other location
     * @param radiusKm the radius in kilometers
     * @return true if within radius, false otherwise
     */
    public boolean isWithinRadius(Location other, double radiusKm) {
        return distanceTo(other) <= radiusKm;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}