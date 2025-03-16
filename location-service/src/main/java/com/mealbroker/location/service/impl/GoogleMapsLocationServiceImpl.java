package com.mealbroker.location.service.impl;

import com.google.maps.*;
import com.google.maps.model.*;
import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.NearestBranchRequestDTO;
import com.mealbroker.location.exception.LocationServiceException;
import com.mealbroker.location.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleMapsLocationServiceImpl implements LocationService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsLocationServiceImpl.class);

    private final GeoApiContext geoApiContext;

    @Autowired
    public GoogleMapsLocationServiceImpl(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    @Override
    public double calculateDistance(Location location1, Location location2) {
        try {
            LatLng origin = new LatLng(location1.getLatitude(), location1.getLongitude());
            LatLng destination = new LatLng(location2.getLatitude(), location2.getLongitude());

            DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(origin)
                    .destinations(destination)
                    .mode(TravelMode.DRIVING);

            DistanceMatrix result = request.await();
            return result.rows[0].elements[0].distance.inMeters / 1000.0;
        } catch (Exception e) {
            logger.error("Error calculating distance between locations", e);
            throw new LocationServiceException("Failed to calculate distance using Google Maps API", e);
        }
    }

    @Override
    public List<Location> findNearbyLocations(Location center, List<Location> locations, double radiusKm) {
        return locations.stream().filter(location -> calculateDistance(center, location) <= radiusKm)
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

        // Create batched distance request to Google Maps API
        Location customerLocation = requestDTO.getCustomerLocation();
        LatLng origin = new LatLng(customerLocation.getLatitude(), customerLocation.getLongitude());

        // Create array of branch locations
        LatLng[] destinations = filteredBranches.stream()
                .map(branch -> new LatLng(
                        branch.getLocation().getLatitude(),
                        branch.getLocation().getLongitude()))
                .toArray(LatLng[]::new);

        try {
            // Request distance matrix from Google Maps API
            DistanceMatrix result = DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(origin)
                    .destinations(destinations)
                    .mode(TravelMode.DRIVING)
                    .await();

            // Find branch with minimum distance
            DistanceMatrixElement[] elements = result.rows[0].elements;

            // Check if any elements are valid
            boolean anyValid = false;
            for (DistanceMatrixElement element : elements) {
                if (element.status == DistanceMatrixElementStatus.OK) {
                    anyValid = true;
                    break;
                }
            }

            if (!anyValid) {
                logger.warn("No valid routes found to any branches");
                return null;
            }

            // Find the branch with minimum distance
            int minIndex = -1;
            long minDistance = Long.MAX_VALUE;

            for (int i = 0; i < elements.length; i++) {
                if (elements[i].status == DistanceMatrixElementStatus.OK) {
                    long distance = elements[i].distance.inMeters;

                    // Apply max distance filter if specified
                    if (requestDTO.getMaxDistanceKm() > 0 &&
                            distance > requestDTO.getMaxDistanceKm() * 1000) {
                        continue;
                    }

                    if (distance < minDistance) {
                        minDistance = distance;
                        minIndex = i;
                    }
                }
            }

            return minIndex >= 0 ? filteredBranches.get(minIndex) : null;

        } catch (Exception e) {
            logger.error("Error finding nearest branch", e);
            throw new LocationServiceException("Failed to find nearest branch using Google Maps API", e);
        }
    }

    @Override
    public List<Branch> findNearbyBranches(List<Branch> branches, Location customerLocation, double maxDistance) {
        if (customerLocation == null || branches == null) {
            throw new IllegalArgumentException("Customer location and branches cannot be null");
        }

        LatLng origin = new LatLng(customerLocation.getLatitude(), customerLocation.getLongitude());

        // Filter active branches first
        List<Branch> activeBranches = branches.stream()
                .filter(Branch::isActive)
                .filter(branch -> branch.getLocation() != null)
                .collect(Collectors.toList());

        if (activeBranches.isEmpty()) {
            return Collections.emptyList();
        }

        // Create array of branch locations
        LatLng[] destinations = activeBranches.stream()
                .map(branch -> new LatLng(
                        branch.getLocation().getLatitude(),
                        branch.getLocation().getLongitude()))
                .toArray(LatLng[]::new);

        try {
            // Request distance matrix from Google Maps API
            DistanceMatrix result = DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(origin)
                    .destinations(destinations)
                    .mode(TravelMode.DRIVING)
                    .await();

            // Filter and sort branches by distance
            List<Branch> nearbyBranches = new ArrayList<>();
            DistanceMatrixElement[] elements = result.rows[0].elements;

            for (int i = 0; i < elements.length; i++) {
                if (elements[i].status == DistanceMatrixElementStatus.OK) {
                    long distanceInMeters = elements[i].distance.inMeters;

                    // Check if branch is within max distance
                    if (distanceInMeters <= maxDistance * 1000) {
                        Branch branch = activeBranches.get(i);
                        // Store distance for sorting (could use a wrapper class in a real implementation)
                        branch.setOperatingRadius((int)(distanceInMeters / 1000));
                        nearbyBranches.add(branch);
                    }
                }
            }

            // Sort by distance
            return nearbyBranches.stream()
                    .sorted(Comparator.comparing(Branch::getOperatingRadius))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error finding nearby branches", e);
            throw new LocationServiceException("Failed to find nearby branches using Google Maps API", e);
        }
    }

    @Override
    public Location geocodeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new LocationServiceException("Address cannot be null or empty");
        }

        try {
            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, address).await();

            if (results.length == 0) {
                throw new LocationServiceException("No results found for address: " + address);
            }

            // Get the first result
            GeocodingResult result = results[0];
            return new Location(
                    result.geometry.location.lat,
                    result.geometry.location.lng
            );

        } catch (Exception e) {
            logger.error("Error geocoding address: " + address, e);
            throw new LocationServiceException("Failed to geocode address using Google Maps API", e);
        }
    }

    @Override
    public String reverseGeocode(Location location) {
        if (location == null) {
            throw new LocationServiceException("Location cannot be null");
        }

        try {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, latLng).await();

            if (results.length == 0) {
                return "Unknown Location";
            }

            // Return the formatted address of the first result
            return results[0].formattedAddress;

        } catch (Exception e) {
            logger.error("Error reverse geocoding location", e);
            throw new LocationServiceException("Failed to reverse geocode location using Google Maps API", e);
        }
    }

    @Override
    public List<Location> findOptimalRoute(Location start, List<Location> destinations) {
        if (start == null || destinations == null || destinations.isEmpty()) {
            throw new LocationServiceException("Start location and destinations cannot be null or empty");
        }

        try {
            if (destinations.size() == 1) {
                List<Location> route = new ArrayList<>();
                route.add(start);
                route.add(destinations.get(0));
                return route;
            }

            // For multiple destinations, use the Directions API with waypoints optimization
            LatLng origin = new LatLng(start.getLatitude(), start.getLongitude());

            Location lastLocation = destinations.get(destinations.size() - 1);
            LatLng destination = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            // Create waypoints from the intermediates
            List<LatLng> waypoints = new ArrayList<>();
            for (int i = 0; i < destinations.size() - 1; i++) {
                Location loc = destinations.get(i);
                waypoints.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
            }

            // Request directions with waypoint optimization
            DirectionsApiRequest request = DirectionsApi.newRequest(geoApiContext)
                    .origin(origin)
                    .destination(destination)
                    .waypoints(waypoints.toArray(new LatLng[0]))
                    .optimizeWaypoints(true)
                    .mode(TravelMode.DRIVING);

            DirectionsResult result = request.await();

            if (result.routes.length == 0) {
                throw new LocationServiceException("No routes found between the specified locations");
            }

            // Get the optimized route
            DirectionsRoute route = result.routes[0];
            
            List<Location> optimizedRoute = new ArrayList<>();
            optimizedRoute.add(start);

            // Add the waypoints in the optimized order
            int[] waypointOrder = route.waypointOrder;
            for (int index : waypointOrder) {
                Location waypointLoc = destinations.get(index);
                optimizedRoute.add(waypointLoc);
            }

            // Add the final destination
            optimizedRoute.add(lastLocation);

            return optimizedRoute;

        } catch (Exception e) {
            logger.error("Error finding optimal route", e);
            throw new LocationServiceException("Failed to find optimal route using Google Directions API", e);
        }
    }
}
