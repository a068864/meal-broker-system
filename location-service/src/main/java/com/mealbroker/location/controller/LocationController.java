package com.mealbroker.location.controller;

import com.mealbroker.domain.Location;
import com.mealbroker.location.dto.LocationRequestDTO;
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
     *
     * @param requestDTO contains origin and destination locations
     * @return distance in kilometers
     */
    @PostMapping("/distance")
    public Double calculateDistance(@Valid @RequestBody LocationRequestDTO requestDTO) {
        return locationService.calculateDistance(requestDTO.getOrigin(), requestDTO.getDestination());
    }

    /**
     * Find locations within a specified radius
     *
     * @param requestDTO contains origin, locations to check, and radius
     * @return list of locations within the radius
     */
    @PostMapping("/nearby")
    public List<Location> findNearbyLocations(@Valid @RequestBody NearbyRequestDTO requestDTO) {
        return locationService.findNearbyLocations(
                requestDTO.getCenter(),
                requestDTO.getLocations(),
                requestDTO.getRadiusKm());
    }

    /**
     * Geocode an address to get coordinates
     *
     * @param address the address to geocode
     * @return the location coordinates
     */
    @GetMapping("/geocode")
    public Location geocodeAddress(@RequestParam String address) {
        return locationService.geocodeAddress(address);
    }

    /**
     * Reverse geocode coordinates to get an address
     *
     * @param location the location to reverse geocode
     * @return the address
     */
    @PostMapping("/reverse-geocode")
    public String reverseGeocode(@Valid @RequestBody Location location) {
        return locationService.reverseGeocode(location);
    }

    /**
     * Find the optimal route from origin to a set of destinations
     *
     * @param requestDTO contains origin and destinations
     * @return ordered list of locations representing the optimal route
     */
    @PostMapping("/optimal-route")
    public List<Location> findOptimalRoute(@Valid @RequestBody RouteRequestDTO requestDTO) {
        return locationService.findOptimalRoute(requestDTO.getOrigin(), requestDTO.getDestinations());
    }

    /**
     * Simple health check endpoint
     *
     * @return health status
     */
    @GetMapping("/health")
    public String health() {
        return "Location Service is up and running!";
    }
}