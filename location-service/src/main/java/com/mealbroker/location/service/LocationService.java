package com.mealbroker.location.service;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.location.dto.NearestBranchRequestDTO;

import java.util.List;

/**
 * Service interface for all location operations
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
     * Find the nearest location from a list of locations
     *
     * @param center    the reference location
     * @param locations list of locations to check
     * @return the nearest location, or null if the list is empty
     */
    Location findNearestLocation(Location center, List<Location> locations);

    /**
     * Find the nearest branch using request DTO
     *
     * @param requestDTO containing customer location, branches list, and filtering options
     * @return the nearest branch, or null if no valid branch is found
     */
    Branch findNearestBranch(NearestBranchRequestDTO requestDTO);

    /**
     * Find nearby branches for a restaurant based on customer location
     *
     * @param branches         list of all branches to consider
     * @param customerLocation the customer location
     * @param maxDistance      the maximum distance in kilometers
     * @return list of nearby branches
     */
    List<Branch> findNearbyBranches(List<Branch> branches, Location customerLocation, double maxDistance);

    /**
     * Check if a location is within a specified radius of another location
     *
     * @param center   the center location
     * @param location the location to check
     * @param radiusKm the radius in kilometers
     * @return true if the location is within the radius, false otherwise
     */
    boolean isWithinRadius(Location center, Location location, double radiusKm);

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