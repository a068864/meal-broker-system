package com.mealbroker.location.service.impl;

import com.mealbroker.domain.Location;
import com.mealbroker.location.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A toy implementation of LocationService using simple latitude and longitude calculations
 */
@Service
public class LocationServiceImpl implements LocationService {

    // Earth's radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public double calculateDistance(Location location1, Location location2) {
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
        return locations.stream()
                .filter(location -> calculateDistance(center, location) <= radiusKm)
                .collect(Collectors.toList());
    }

    @Override
    public Location geocodeAddress(String address) {
        // In a real implementation, this would call the Google Map API
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
                // Default unknown addresses
                return new Location(0.0, 0.0);
        }
    }

    @Override
    public String reverseGeocode(Location location) {
        // In a real implementation, this would call the Google Map API

        // List of predefined cities with coordinates
        List<CityLocation> cities = new ArrayList<>();
        cities.add(new CityLocation("Toronto", 43.6532, -79.3832));
        cities.add(new CityLocation("Vancouver", 49.2827, -123.1207));
        cities.add(new CityLocation("Montreal", 45.5017, -73.5673));
        cities.add(new CityLocation("Calgary", 51.0447, -114.0719));
        cities.add(new CityLocation("Ottawa", 45.4215, -75.6972));
        cities.add(new CityLocation("Edmonton", 53.5461, -113.4938));
        cities.add(new CityLocation("Quebec City", 46.8139, -71.2080));
        cities.add(new CityLocation("Winnipeg", 49.8951, -97.1384));

        // Define a maximum distance threshold (in km) to consider a location as "known"
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
        if (minDistance > MAX_DISTANCE_THRESHOLD) {
            return "Unknown Location";
        }
        return nearestCity.name;
    }

    @Override
    public List<Location> findOptimalRoute(Location start, List<Location> destinations) {
        // Not actually optimal (not solving TSP), but simple for demonstration purposes

        List<Location> route = new ArrayList<>();
        route.add(start);

        // Filter out the starting location from destinations to avoid duplicates
        List<Location> remaining = new ArrayList<>(destinations);

        Location current = start;
        while (!remaining.isEmpty()) {
            // Find nearest unvisited location
            Location next = current;
            double minDistance = Double.MAX_VALUE;
            for (Location location : remaining) {
                if (location.equals(current)) {
                    continue;
                }
                double dist = calculateDistance(current, location);
                if (dist <= minDistance) {
                    minDistance = dist;
                    next = location;
                }
            }
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