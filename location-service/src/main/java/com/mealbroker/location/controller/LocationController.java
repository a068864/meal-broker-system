package com.mealbroker.location.controller;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.location.dto.LocationRequestDTO;
import com.mealbroker.location.dto.NearbyBranchRequestDTO;
import com.mealbroker.location.dto.NearbyRequestDTO;
import com.mealbroker.location.dto.RouteRequestDTO;
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
     * Find locations within a specified radius
     */
    @PostMapping("/nearby")
    public List<Location> findNearbyLocations(@Valid @RequestBody NearbyRequestDTO requestDTO) {
        return locationService.findNearbyLocations(
                requestDTO.getCenter(),
                requestDTO.getLocations(),
                requestDTO.getRadiusKm());
    }

    /**
     * Find the nearest branch to a customer location
     */
    @PostMapping("/nearest-branch")
    public Branch findNearestBranch(
            @RequestParam Location customerLocation,
            @RequestBody List<Branch> branches) {
        return locationService.findNearestBranch(customerLocation, branches);
    }

    /**
     * Find nearby branches based on customer location
     */
    @PostMapping("/nearby-branches")
    public List<Branch> findNearbyBranches(
            @Valid @RequestBody NearbyBranchRequestDTO requestDTO) {
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