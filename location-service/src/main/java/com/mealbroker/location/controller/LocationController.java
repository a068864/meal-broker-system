package com.mealbroker.location.controller;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.location.dto.*;
import com.mealbroker.location.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for location operations
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Calculate distance between two locations
     */
    @PostMapping("/distance")
    public Double calculateDistance(@Valid @RequestBody LocationRequestDTO requestDTO) {
        return locationService.calculateDistance(requestDTO.getOrigin(), requestDTO.getDestination());
    }

    /**
     * Find the nearest branch to a customer location
     */
    @PostMapping("/nearest-branch")
    public Branch findNearestBranch(@Valid @RequestBody NearestBranchRequestDTO requestDTO) {
        return locationService.findNearestBranch(requestDTO);
    }

    /**
     * Find nearby branches based on customer location
     */
    @PostMapping("/nearby-branches")
    public List<Branch> findNearbyBranches(
            @Valid @RequestBody NearbyBranchesRequestDTO requestDTO) {
        return locationService.findNearbyBranches(
                requestDTO.getBranches(),
                requestDTO.getCustomerLocation(),
                requestDTO.getMaxDistanceKm());
    }

    /**
     * Check if a location is within a specified radius
     */
    @PostMapping("/within-radius")
    public boolean isWithinRadius(
            @RequestParam Location center,
            @RequestParam Location location,
            @RequestParam double radiusKm) {
        return locationService.isWithinRadius(center, location, radiusKm);
    }

    /**
     * Geocode an address to get coordinates
     */
    @GetMapping("/geocode")
    public Location geocodeAddress(@RequestParam String address) {
        return locationService.geocodeAddress(address);
    }

    /**
     * Reverse geocode coordinates to get an address
     */
    @PostMapping("/reverse-geocode")
    public String reverseGeocode(@Valid @RequestBody Location location) {
        return locationService.reverseGeocode(location);
    }

    /**
     * Find the optimal route from origin to a set of destinations
     */
    @PostMapping("/optimal-route")
    public List<Location> findOptimalRoute(@Valid @RequestBody RouteRequestDTO requestDTO) {
        return locationService.findOptimalRoute(requestDTO.getOrigin(), requestDTO.getDestinations());
    }

    /**
     * Simple health check endpoint
     */
    @GetMapping("/health")
    public String health() {
        return "Location Service is up and running!";
    }
}