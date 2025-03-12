package com.mealbroker.broker.client;

import com.mealbroker.broker.dto.LocationRequestDTO;
import com.mealbroker.broker.dto.NearbyRequestDTO;
import com.mealbroker.broker.dto.RouteRequestDTO;
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
     *
     * @param request the request containing origin and destination locations
     * @return the distance in kilometers
     */
    @PostMapping("/api/locations/distance")
    Double calculateDistance(@RequestBody LocationRequestDTO request);

    /**
     * Find nearby locations within a specified radius
     *
     * @param request the request containing origin, locations list, and distance threshold
     * @return list of locations within the specified radius
     */
    @PostMapping("/api/locations/nearby")
    List<Location> findNearbyLocations(@RequestBody NearbyRequestDTO request);

    /**
     * Geocode an address to get location coordinates
     *
     * @param address the address to geocode
     * @return the location coordinates
     */
    @GetMapping("/api/locations/geocode")
    Location geocodeAddress(@RequestParam String address);

    /**
     * Reverse geocode coordinates to get an address
     *
     * @param location the location to reverse geocode
     * @return the address as a string
     */
    @PostMapping("/api/locations/reverse-geocode")
    String reverseGeocode(@RequestBody Location location);

    /**
     * Find the optimal route from an origin to a list of destinations
     *
     * @param request the request containing origin and destinations
     * @return ordered list of locations representing the optimal route
     */
    @PostMapping("/api/locations/optimal-route")
    List<Location> findOptimalRoute(@RequestBody RouteRequestDTO request);

    /**
     * Health check endpoint
     *
     * @return health status
     */
    @GetMapping("/api/locations/health")
    String health();
}