// order-broker/src/main/java/com/mealbroker/broker/client/LocationServiceClient.java
package com.mealbroker.broker.client;

import com.mealbroker.broker.dto.LocationRequestDTO;
import com.mealbroker.broker.dto.NearbyBranchRequestDTO;
import com.mealbroker.broker.dto.NearbyRequestDTO;
import com.mealbroker.broker.dto.RouteRequestDTO;
import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for the Location Service
 */
@FeignClient(name = "location-service")
public interface LocationServiceClient {

    /**
     * Calculate distance between two locations
     */
    @PostMapping("/api/locations/distance")
    Double calculateDistance(@RequestBody LocationRequestDTO request);

    /**
     * Find nearby locations within a specified radius
     */
    @PostMapping("/api/locations/nearby")
    List<Location> findNearbyLocations(@RequestBody NearbyRequestDTO request);

    /**
     * Find the nearest branch from a list of branches
     */
    @PostMapping("/api/locations/nearest-branch")
    Branch findNearestBranch(
            @RequestParam Location customerLocation,
            @RequestBody List<Branch> branches);

    /**
     * Find nearby branches based on customer location
     */
    @PostMapping("/api/locations/nearby-branches")
    List<Branch> findNearbyBranches(@RequestBody NearbyBranchRequestDTO requestDTO);

    /**
     * Check if a location is within a specific radius
     */
    @PostMapping("/api/locations/within-radius")
    boolean isWithinRadius(
            @RequestParam Location center,
            @RequestParam Location location,
            @RequestParam double radiusKm);

    /**
     * Geocode an address to get location coordinates
     */
    @GetMapping("/api/locations/geocode")
    Location geocodeAddress(@RequestParam String address);

    /**
     * Reverse geocode coordinates to get an address
     */
    @PostMapping("/api/locations/reverse-geocode")
    String reverseGeocode(@RequestBody Location location);

    /**
     * Find the optimal route from an origin to a list of destinations
     */
    @PostMapping("/api/locations/optimal-route")
    List<Location> findOptimalRoute(@RequestBody RouteRequestDTO request);

    /**
     * Health check endpoint
     */
    @GetMapping("/api/locations/health")
    String health();
}