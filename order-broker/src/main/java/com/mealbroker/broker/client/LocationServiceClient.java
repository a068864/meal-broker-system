package com.mealbroker.broker.client;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.LocationRequestDTO;
import com.mealbroker.domain.dto.NearbyBranchesRequestDTO;
import com.mealbroker.domain.dto.NearestBranchRequestDTO;
import com.mealbroker.domain.dto.RouteRequestDTO;
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
     * Find the nearest branch using request DTO
     */
    @PostMapping("/api/locations/nearest-branch")
    Branch findNearestBranch(@RequestBody NearestBranchRequestDTO requestDTO);

    /**
     * Find nearby branches based on customer location
     */
    @PostMapping("/api/locations/nearby-branches")
    List<Branch> findNearbyBranches(@RequestBody NearbyBranchesRequestDTO requestDTO);

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