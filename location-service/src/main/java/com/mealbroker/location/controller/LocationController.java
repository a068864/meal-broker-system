package com.mealbroker.location.controller;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.LocationRequestDTO;
import com.mealbroker.domain.dto.NearbyBranchesRequestDTO;
import com.mealbroker.domain.dto.NearestBranchRequestDTO;
import com.mealbroker.domain.dto.RouteRequestDTO;
import com.mealbroker.location.service.LocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for location operations
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

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
        logger.info("Calculating distance between: {} and {}", requestDTO.getOrigin(), requestDTO.getDestination());
        return locationService.calculateDistance(requestDTO.getOrigin(), requestDTO.getDestination());
    }

    /**
     * Find the nearest branch to a customer location
     */
    @PostMapping("/nearest-branch")
    public Branch findNearestBranch(@Valid @RequestBody NearestBranchRequestDTO requestDTO) {
        logger.info("Finding nearest branch from {} branches",
                requestDTO.getBranches() != null ? requestDTO.getBranches().size() : 0);

        Branch nearestBranch = locationService.findNearestBranch(requestDTO);
        if (nearestBranch == null) {
            // Log the issue but return null so the client can handle it appropriately
            logger.warn("No suitable branch found for customer location: {}", requestDTO.getCustomerLocation());
        } else {
            logger.info("Found nearest branch: {} with ID: {}", nearestBranch.getBranchName(), nearestBranch.getBranchId());
        }
        return nearestBranch;
    }

    /**
     * Find nearby branches based on customer location
     */
    @PostMapping("/nearby-branches")
    public List<Branch> findNearbyBranches(@Valid @RequestBody NearbyBranchesRequestDTO requestDTO) {
        logger.info("Finding nearby branches within {} km", requestDTO.getMaxDistanceKm());

        List<Branch> nearbyBranches = locationService.findNearbyBranches(
                requestDTO.getBranches(),
                requestDTO.getCustomerLocation(),
                requestDTO.getMaxDistanceKm());

        logger.info("Found {} nearby branches", nearbyBranches.size());
        return nearbyBranches;
    }

    /**
     * Geocode an address to get coordinates
     */
    @GetMapping("/geocode")
    public Location geocodeAddress(@RequestParam String address) {
        logger.info("Geocoding address: {}", address);
        return locationService.geocodeAddress(address);
    }

    /**
     * Reverse geocode coordinates to get an address
     */
    @PostMapping("/reverse-geocode")
    public String reverseGeocode(@Valid @RequestBody Location location) {
        logger.info("Reverse geocoding location: {}", location);
        return locationService.reverseGeocode(location);
    }

    /**
     * Find the optimal route from origin to a set of destinations
     */
    @PostMapping("/optimal-route")
    public List<Location> findOptimalRoute(@Valid @RequestBody RouteRequestDTO requestDTO) {
        logger.info("Finding optimal route for {} destinations",
                requestDTO.getDestinations() != null ? requestDTO.getDestinations().size() : 0);
        return locationService.findOptimalRoute(requestDTO.getOrigin(), requestDTO.getDestinations());
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Location Service is up and running!");
    }
}