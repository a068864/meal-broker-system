package com.mealbroker.location.service.impl;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.NearestBranchRequestDTO;
import com.mealbroker.location.exception.LocationServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocationServiceImplTest {

    private LocationServiceImpl locationService;
    private Location toronto;
    private Location vancouver;
    private Location montreal;
    private Location calgary;
    private List<Branch> branches;

    @BeforeEach
    void setUp() {
        locationService = new LocationServiceImpl();

        // Initialize common test locations
        toronto = new Location(43.6532, -79.3832);
        vancouver = new Location(49.2827, -123.1207);
        montreal = new Location(45.5017, -73.5673);
        calgary = new Location(51.0447, -114.0719);

        // Set up test branches
        branches = new ArrayList<>();

        Branch branch1 = new Branch("Downtown Toronto", toronto);
        branch1.setBranchId(1L);
        branch1.setActive(true);

        Branch branch2 = new Branch("Downtown Vancouver", vancouver);
        branch2.setBranchId(2L);
        branch2.setActive(true);

        Branch branch3 = new Branch("Downtown Montreal", montreal);
        branch3.setBranchId(3L);
        branch3.setActive(false);  // Inactive branch

        Branch branch4 = new Branch("Downtown Calgary", calgary);
        branch4.setBranchId(4L);
        branch4.setActive(true);

        branches.addAll(Arrays.asList(branch1, branch2, branch3, branch4));
    }

    @Test
    void calculateDistance_shouldReturnCorrectDistance() {
        // Toronto to Montreal (approximately 504 km)
        double distance = locationService.calculateDistance(toronto, montreal);
        assertEquals(504.0, distance, 5.0); // Allow 5km tolerance

        // Vancouver to Calgary (approximately 675 km)
        distance = locationService.calculateDistance(vancouver, calgary);
        assertEquals(675.0, distance, 10.0); // Allow 10km tolerance

        // Distance should be symmetrical
        double distanceA = locationService.calculateDistance(toronto, montreal);
        double distanceB = locationService.calculateDistance(montreal, toronto);
        assertEquals(distanceA, distanceB, 0.001);
    }

    @Test
    void calculateDistance_withNullParameters_shouldThrowException() {
        // Both null
        assertThrows(IllegalArgumentException.class, () ->
                locationService.calculateDistance(null, null));

        // One null
        assertThrows(IllegalArgumentException.class, () ->
                locationService.calculateDistance(toronto, null));

        assertThrows(IllegalArgumentException.class, () ->
                locationService.calculateDistance(null, montreal));
    }

    @Test
    void findNearbyLocations_shouldReturnCorrectLocations() {
        Location mississauga = new Location(43.5890, -79.6441);
        List<Location> locations = Arrays.asList(toronto, vancouver, montreal, calgary);

        // Search within 50km of Mississauga
        List<Location> nearbyLocations = locationService.findNearbyLocations(mississauga, locations, 50.0);
        assertEquals(1, nearbyLocations.size());
        assertTrue(nearbyLocations.contains(toronto));

        // Search within 600km of Mississauga
        nearbyLocations = locationService.findNearbyLocations(mississauga, locations, 600.0);
        assertEquals(2, nearbyLocations.size());
        assertTrue(nearbyLocations.contains(toronto));
        assertTrue(nearbyLocations.contains(montreal));
    }

    @Test
    void findNearbyLocations_withNullParameters_shouldThrowException() {
        List<Location> locations = Arrays.asList(toronto, vancouver, montreal, calgary);

        // Null center
        assertThrows(IllegalArgumentException.class, () ->
                locationService.findNearbyLocations(null, locations, 50.0));

        // Null locations
        assertThrows(IllegalArgumentException.class, () ->
                locationService.findNearbyLocations(toronto, null, 50.0));
    }

    @Test
    void findNearestLocation_shouldReturnCorrectLocation() {
        Location mississauga = new Location(43.5890, -79.6441);
        List<Location> locations = Arrays.asList(toronto, vancouver, montreal, calgary);

        // Nearest to Mississauga should be Toronto
        Location nearest = locationService.findNearestLocation(mississauga, locations);
        assertEquals(toronto, nearest);

        // Nearest to Vancouver should be Vancouver itself
        nearest = locationService.findNearestLocation(vancouver, locations);
        assertEquals(vancouver, nearest);
    }

    @Test
    void findNearestLocation_withEmptyOrNullParameters_shouldReturnNull() {
        List<Location> locations = Arrays.asList(toronto, vancouver, montreal, calgary);

        // Null center
        assertNull(locationService.findNearestLocation(null, locations));

        // Null locations list
        assertNull(locationService.findNearestLocation(toronto, null));

        // Empty locations list
        assertNull(locationService.findNearestLocation(toronto, Collections.emptyList()));
    }

    @Test
    void findNearestBranch_shouldReturnCorrectBranch() {
        // Customer in Mississauga (near Toronto)
        Location mississauga = new Location(43.5890, -79.6441);

        // Create a branch that's actually close to Mississauga
        Branch closeToMississauga = new Branch("Mississauga Branch",
                new Location(43.5890, -79.6450)); // Very close to Mississauga
        closeToMississauga.setBranchId(5L);
        closeToMississauga.setActive(true);
        branches.add(closeToMississauga);

        // Request with all branches, active only
        NearestBranchRequestDTO request = new NearestBranchRequestDTO(mississauga, branches, true);
        Branch nearest = locationService.findNearestBranch(request);
        assertNotNull(nearest);
        assertEquals(5L, nearest.getBranchId()); // Should be Mississauga branch

        // Request with all branches, including inactive
        request = new NearestBranchRequestDTO(mississauga, branches, false);
        nearest = locationService.findNearestBranch(request);
        assertNotNull(nearest);
        assertEquals(5L, nearest.getBranchId()); // Still Mississauga branch

        // Request with max distance that excludes all branches
        request = new NearestBranchRequestDTO(mississauga, branches, true, 0.001);
        nearest = locationService.findNearestBranch(request);
        assertNull(nearest); // No branches within 1 meter
    }

    @Test
    void findNearestBranch_withEmptyOrNullParameters_shouldReturnNull() {
        Location mississauga = new Location(43.5890, -79.6441);

        // Null request
        assertNull(locationService.findNearestBranch(null));

        // Null customer location
        NearestBranchRequestDTO request = new NearestBranchRequestDTO(null, branches, true);
        assertNull(locationService.findNearestBranch(request));

        // Null branches list
        request = new NearestBranchRequestDTO(mississauga, null, true);
        assertNull(locationService.findNearestBranch(request));

        // Empty branches list
        request = new NearestBranchRequestDTO(mississauga, Collections.emptyList(), true);
        assertNull(locationService.findNearestBranch(request));
    }

    @Test
    void findNearbyBranches_shouldReturnCorrectBranches() {
        // Customer in Mississauga (near Toronto)
        Location mississauga = new Location(43.5890, -79.6441);

        // Find branches within 100km (only Toronto)
        List<Branch> nearbyBranches = locationService.findNearbyBranches(branches, mississauga, 100.0);
        assertEquals(1, nearbyBranches.size());
        assertEquals(1L, nearbyBranches.get(0).getBranchId());

        // Find branches within 1000km (Toronto and Montreal, but not Montreal since it's inactive)
        nearbyBranches = locationService.findNearbyBranches(branches, mississauga, 1000.0);
        assertEquals(1, nearbyBranches.size());
        assertEquals(1L, nearbyBranches.get(0).getBranchId());
    }

    @Test
    void findNearbyBranches_withNullParameters_shouldThrowException() {
        Location mississauga = new Location(43.5890, -79.6441);

        // Null customer location
        assertThrows(IllegalArgumentException.class, () ->
                locationService.findNearbyBranches(branches, null, 100.0));

        // Null branches list
        assertThrows(IllegalArgumentException.class, () ->
                locationService.findNearbyBranches(null, mississauga, 100.0));
    }

    @Test
    void geocodeAddress_shouldReturnCorrectLocation() {
        // Test known addresses
        Location location = locationService.geocodeAddress("toronto");
        assertEquals(toronto.getLatitude(), location.getLatitude());
        assertEquals(toronto.getLongitude(), location.getLongitude());

        location = locationService.geocodeAddress("vancouver");
        assertEquals(vancouver.getLatitude(), location.getLatitude());
        assertEquals(vancouver.getLongitude(), location.getLongitude());

        // Unknown address should return default location
        location = locationService.geocodeAddress("unknown address");
        assertEquals(0.0, location.getLatitude());
        assertEquals(0.0, location.getLongitude());
    }

    @Test
    void geocodeAddress_withNullOrEmptyAddress_shouldThrowException() {
        // Null address
        assertThrows(LocationServiceException.class, () ->
                locationService.geocodeAddress(null));

        // Empty address
        assertThrows(LocationServiceException.class, () ->
                locationService.geocodeAddress(""));

        // Whitespace-only address
        assertThrows(LocationServiceException.class, () ->
                locationService.geocodeAddress("   "));
    }

    @Test
    void reverseGeocode_shouldReturnCorrectAddress() {
        // Test known locations
        String address = locationService.reverseGeocode(toronto);
        assertEquals("Toronto", address);

        address = locationService.reverseGeocode(vancouver);
        assertEquals("Vancouver", address);

        // Unknown location should return "Unknown Location"
        Location unknownLocation = new Location(0.0, 0.0);
        address = locationService.reverseGeocode(unknownLocation);
        assertEquals("Unknown Location", address);
    }

    @Test
    void reverseGeocode_withNullLocation_shouldThrowException() {
        assertThrows(LocationServiceException.class, () ->
                locationService.reverseGeocode(null));
    }

    @Test
    void findOptimalRoute_shouldReturnCorrectRoute() {
        Location mississauga = new Location(43.5890, -79.6441);
        List<Location> destinations = Arrays.asList(toronto, montreal, vancouver, calgary);

        // Starting from Mississauga
        List<Location> route = locationService.findOptimalRoute(mississauga, destinations);

        // Route should start with Mississauga
        assertEquals(mississauga, route.get(0));

        // Next should be Toronto (nearest to Mississauga)
        assertEquals(toronto, route.get(1));

        // The rest of the route should contain all destinations
        assertEquals(5, route.size());
        assertTrue(route.contains(toronto));
        assertTrue(route.contains(montreal));
        assertTrue(route.contains(vancouver));
        assertTrue(route.contains(calgary));
    }

    @Test
    void findOptimalRoute_withNullParameters_shouldThrowException() {
        List<Location> destinations = Arrays.asList(toronto, montreal, vancouver, calgary);

        // Null start
        assertThrows(LocationServiceException.class, () ->
                locationService.findOptimalRoute(null, destinations));

        // Null destinations
        assertThrows(LocationServiceException.class, () ->
                locationService.findOptimalRoute(toronto, null));
    }
}