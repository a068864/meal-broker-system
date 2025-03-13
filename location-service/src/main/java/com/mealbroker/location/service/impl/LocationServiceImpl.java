package com.mealbroker.location.service.impl;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.NearestBranchRequestDTO;
import com.mealbroker.location.exception.LocationServiceException;
import com.mealbroker.location.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of LocationService using Haversine formula
 */
@Service
public class LocationServiceImpl implements LocationService {

    // Earth's radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public double calculateDistance(Location location1, Location location2) {
        if (location1 == null || location2 == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }

        // Implementation of the Haversine formula to calculate distance between two points on Earth
        double lat1 = Math.toRadians(location1.getLatitude());
        double lon1 = Math.toRadians(location1.getLongitude());
        double lat2 = Math.toRadians(location2.getLatitude());
        double lon2 = Math.toRadians(location2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    @Override
    public List<Location> findNearbyLocations(Location center, List<Location> locations, double radiusKm) {
        if (center == null || locations == null) {
            throw new IllegalArgumentException("Center and locations cannot be null");
        }

        return locations.stream()
                .filter(location -> calculateDistance(center, location) <= radiusKm)
                .collect(Collectors.toList());
    }

    @Override
    public Location findNearestLocation(Location center, List<Location> locations) {
        if (center == null || locations == null || locations.isEmpty()) {
            return null;
        }

        return locations.stream()
                .min(Comparator.comparingDouble(location -> calculateDistance(center, location)))
                .orElse(null);
    }

    @Override
    public Branch findNearestBranch(NearestBranchRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getCustomerLocation() == null ||
                requestDTO.getBranches() == null || requestDTO.getBranches().isEmpty()) {
            return null;
        }

        // Filter branches if activeOnly is true
        List<Branch> filteredBranches = requestDTO.getBranches();
        if (requestDTO.isActiveOnly()) {
            filteredBranches = filteredBranches.stream()
                    .filter(Branch::isActive)
                    .filter(branch -> branch.getLocation() != null)
                    .collect(Collectors.toList());

            if (filteredBranches.isEmpty()) {
                return null;
            }
        }

        // Filter by maximum distance if specified
        if (requestDTO.getMaxDistanceKm() > 0) {
            final Location customerLocation = requestDTO.getCustomerLocation();
            final double maxDistance = requestDTO.getMaxDistanceKm();

            filteredBranches = filteredBranches.stream()
                    .filter(branch -> calculateDistance(customerLocation, branch.getLocation()) <= maxDistance)
                    .collect(Collectors.toList());

            if (filteredBranches.isEmpty()) {
                return null;
            }
        }

        return filteredBranches.stream()
                .min(Comparator.comparingDouble(branch ->
                        calculateDistance(requestDTO.getCustomerLocation(), branch.getLocation())))
                .orElse(null);
    }

    @Override
    public List<Branch> findNearbyBranches(List<Branch> branches, Location customerLocation, double maxDistance) {
        if (customerLocation == null || branches == null) {
            throw new IllegalArgumentException("Customer location and branches cannot be null");
        }

        // Filter by active status and distance
        return branches.stream()
                .filter(Branch::isActive)
                .filter(branch -> branch.getLocation() != null)
                .filter(branch -> calculateDistance(customerLocation, branch.getLocation()) <= maxDistance)
                .sorted(Comparator.comparingDouble(branch ->
                        calculateDistance(customerLocation, branch.getLocation())))
                .collect(Collectors.toList());
    }

    @Override
    public Location geocodeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new LocationServiceException("Address cannot be null or empty");
        }

        // Mock implementation for geocoding
        switch (address.toLowerCase()) {
            case "toronto":
                return new Location(43.6532, -79.3832);
            case "vancouver":
                return new Location(49.2827, -123.1207);
            case "montreal":
                return new Location(45.5017, -73.5673);
            case "calgary":
                return new Location(51.0447, -114.0719);
            case "ottawa":
                return new Location(45.4215, -75.6972);
            case "edmonton":
                return new Location(53.5461, -113.4938);
            case "quebec city":
                return new Location(46.8139, -71.2080);
            case "winnipeg":
                return new Location(49.8951, -97.1384);
            default:
                // For unknown addresses, return a default location
                return new Location(0.0, 0.0);
        }
    }

    @Override
    public String reverseGeocode(Location location) {
        if (location == null) {
            throw new LocationServiceException("Location cannot be null");
        }

        // Mock implementation for reverse geocoding
        List<CityLocation> cities = new ArrayList<>();
        cities.add(new CityLocation("Toronto", 43.6532, -79.3832));
        cities.add(new CityLocation("Vancouver", 49.2827, -123.1207));
        cities.add(new CityLocation("Montreal", 45.5017, -73.5673));
        cities.add(new CityLocation("Calgary", 51.0447, -114.0719));
        cities.add(new CityLocation("Ottawa", 45.4215, -75.6972));
        cities.add(new CityLocation("Edmonton", 53.5461, -113.4938));
        cities.add(new CityLocation("Quebec City", 46.8139, -71.2080));
        cities.add(new CityLocation("Winnipeg", 49.8951, -97.1384));

        final double MAX_DISTANCE_THRESHOLD = 100.0;

        CityLocation nearestCity = null;
        double minDistance = Double.MAX_VALUE;
        for (CityLocation city : cities) {
            Location cityLocation = new Location(city.latitude, city.longitude);
            double distance = calculateDistance(cityLocation, location);
            if (distance < minDistance) {
                minDistance = distance;
                nearestCity = city;
            }
        }
        if (nearestCity == null || minDistance > MAX_DISTANCE_THRESHOLD) {
            return "Unknown Location";
        }
        return nearestCity.name;
    }

    @Override
    public List<Location> findOptimalRoute(Location start, List<Location> destinations) {
        if (start == null || destinations == null) {
            throw new LocationServiceException("Start location and destinations cannot be null");
        }

        // Simple implementation of Nearest Neighbor algorithm for route optimization
        List<Location> route = new ArrayList<>();
        route.add(start);

        // Filter out the starting location from destinations to avoid duplicates
        List<Location> remaining = new ArrayList<>(destinations);
        remaining.removeIf(location ->
                location.getLatitude().equals(start.getLatitude()) &&
                        location.getLongitude().equals(start.getLongitude())
        );

        Location current = start;
        while (!remaining.isEmpty()) {
            // Find nearest unvisited location
            Location next = findNearestLocation(current, remaining);
            if (next == null) break;

            remaining.remove(next);
            route.add(next);
            current = next;
        }
        return route;
    }

    /**
     * Helper class to store city name with coordinates
     */
    private static class CityLocation {
        private String name;
        private double latitude;
        private double longitude;

        public CityLocation(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}