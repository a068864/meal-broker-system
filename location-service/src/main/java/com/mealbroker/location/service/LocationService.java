package com.mealbroker.location.service;

import com.mealbroker.domain.Location;

import java.util.List;

/**
 * Service interface for location operations
 */
public interface LocationService {

    /**
     * Calculate distance between two locations in kilometers
     * Uses the Haversine formula for calculating distance over the Earth's surface
     *
     * @param location1 the first location
     * @param location2 the second location
     * @return distance in kilometers
     */
    double calculateDistance(Location location1, Location location2);

    /**
     * Find nearby locations within the specified radius
     *
     * @param center    the center location
     * @param locations list of locations to check
     * @param radiusKm  the radius in kilometers
     * @return list of locations within the radius
     */
    List<Location> findNearbyLocations(Location center, List<Location> locations, double radiusKm);

    /**
     * Geocode an address to get coordinates
     * (Toy implementation with mock data)
     *
     * @param address the address to geocode
     * @return location with latitude and longitude
     */
    Location geocodeAddress(String address);

    /**
     * Get address from coordinates (reverse geocoding)
     * (Toy implementation with mock data)
     *
     * @param location the location to reverse geocode
     * @return string representation of the address
     */
    String reverseGeocode(Location location);

    /**
     * Find optimal route between locations
     * (Toy implementation using nearest-neighbor approach)
     *
     * @param start        the starting location
     * @param destinations list of destination locations
     * @return ordered list of locations representing the route
     */
    List<Location> findOptimalRoute(Location start, List<Location> destinations);
}