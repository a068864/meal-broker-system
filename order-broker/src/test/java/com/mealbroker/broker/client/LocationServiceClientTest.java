package com.mealbroker.broker.client;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.LocationRequestDTO;
import com.mealbroker.domain.dto.NearbyBranchesRequestDTO;
import com.mealbroker.domain.dto.NearestBranchRequestDTO;
import com.mealbroker.domain.dto.RouteRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceClientTest {

    @Mock
    private LocationServiceClient locationServiceClient;

    private Location testLocation1;
    private Location testLocation2;
    private List<Branch> testBranches;
    private Branch testBranch;
    private LocationRequestDTO testLocationRequest;
    private NearestBranchRequestDTO testNearestBranchRequest;
    private NearbyBranchesRequestDTO testNearbyBranchesRequest;
    private RouteRequestDTO testRouteRequest;
    private List<Location> testLocations;

    @BeforeEach
    void setUp() {
        // Ensure that the interface has required annotations
        assertTrue(LocationServiceClient.class.isAnnotationPresent(FeignClient.class),
                "LocationServiceClient should be annotated with @FeignClient");

        FeignClient feignClient = LocationServiceClient.class.getAnnotation(FeignClient.class);
        assertEquals("location-service", feignClient.name(),
                "FeignClient name should be 'location-service'");

        // Create test data
        testLocation1 = new Location(43.6532, -79.3832); // Toronto
        testLocation2 = new Location(45.5017, -73.5673); // Montreal

        // Create test branches
        testBranches = new ArrayList<>();

        Branch branch1 = new Branch();
        branch1.setBranchId(1L);
        branch1.setBranchName("Test Branch 1");
        branch1.setLocation(new Location(43.6532, -79.3832));
        branch1.setActive(true);

        Branch branch2 = new Branch();
        branch2.setBranchId(2L);
        branch2.setBranchName("Test Branch 2");
        branch2.setLocation(new Location(43.7532, -79.4832));
        branch2.setActive(false);

        testBranches.add(branch1);
        testBranches.add(branch2);
        testBranch = branch1;

        // Create test requests
        testLocationRequest = new LocationRequestDTO(testLocation1, testLocation2);
        testNearestBranchRequest = new NearestBranchRequestDTO(testLocation1, testBranches, true);
        testNearbyBranchesRequest = new NearbyBranchesRequestDTO(testLocation1, testBranches, 10.0);

        testLocations = Arrays.asList(testLocation1, testLocation2);
        testRouteRequest = new RouteRequestDTO(testLocation1, testLocations);
    }

    @Test
    void calculateDistanceMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = LocationServiceClient.class.getMethod("calculateDistance", LocationRequestDTO.class);
        assertTrue(method.isAnnotationPresent(PostMapping.class),
                "calculateDistance method should be annotated with @PostMapping");

        // Test with mock
        when(locationServiceClient.calculateDistance(any(LocationRequestDTO.class))).thenReturn(500.5);

        Double distance = locationServiceClient.calculateDistance(testLocationRequest);

        assertNotNull(distance);
        assertEquals(500.5, distance);

        verify(locationServiceClient, times(1)).calculateDistance(testLocationRequest);
    }

    @Test
    void findNearestBranchMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = LocationServiceClient.class.getMethod("findNearestBranch", NearestBranchRequestDTO.class);
        assertTrue(method.isAnnotationPresent(PostMapping.class),
                "findNearestBranch method should be annotated with @PostMapping");

        // Test with mock
        when(locationServiceClient.findNearestBranch(any(NearestBranchRequestDTO.class))).thenReturn(testBranch);

        Branch branch = locationServiceClient.findNearestBranch(testNearestBranchRequest);

        assertNotNull(branch);
        assertEquals(1L, branch.getBranchId());
        assertEquals("Test Branch 1", branch.getBranchName());
        assertTrue(branch.isActive());

        verify(locationServiceClient, times(1)).findNearestBranch(testNearestBranchRequest);
    }

    @Test
    void findNearbyBranchesMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = LocationServiceClient.class.getMethod("findNearbyBranches", NearbyBranchesRequestDTO.class);
        assertTrue(method.isAnnotationPresent(PostMapping.class),
                "findNearbyBranches method should be annotated with @PostMapping");

        // Test with mock
        when(locationServiceClient.findNearbyBranches(any(NearbyBranchesRequestDTO.class))).thenReturn(testBranches);

        List<Branch> branches = locationServiceClient.findNearbyBranches(testNearbyBranchesRequest);

        assertNotNull(branches);
        assertEquals(2, branches.size());

        verify(locationServiceClient, times(1)).findNearbyBranches(testNearbyBranchesRequest);
    }

    @Test
    void geocodeAddressMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = LocationServiceClient.class.getMethod("geocodeAddress", String.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class),
                "geocodeAddress method should be annotated with @GetMapping");

        // Test with mock
        when(locationServiceClient.geocodeAddress("Toronto")).thenReturn(testLocation1);

        Location location = locationServiceClient.geocodeAddress("Toronto");

        assertNotNull(location);
        assertEquals(testLocation1.getLatitude(), location.getLatitude());
        assertEquals(testLocation1.getLongitude(), location.getLongitude());

        verify(locationServiceClient, times(1)).geocodeAddress("Toronto");
    }

    @Test
    void reverseGeocodeMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = LocationServiceClient.class.getMethod("reverseGeocode", Location.class);
        assertTrue(method.isAnnotationPresent(PostMapping.class),
                "reverseGeocode method should be annotated with @PostMapping");

        // Test with mock
        when(locationServiceClient.reverseGeocode(testLocation1)).thenReturn("Toronto, ON, Canada");

        String address = locationServiceClient.reverseGeocode(testLocation1);

        assertNotNull(address);
        assertEquals("Toronto, ON, Canada", address);

        verify(locationServiceClient, times(1)).reverseGeocode(testLocation1);
    }

    @Test
    void findOptimalRouteMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = LocationServiceClient.class.getMethod("findOptimalRoute", RouteRequestDTO.class);
        assertTrue(method.isAnnotationPresent(PostMapping.class),
                "findOptimalRoute method should be annotated with @PostMapping");

        // Test with mock
        when(locationServiceClient.findOptimalRoute(any(RouteRequestDTO.class))).thenReturn(testLocations);

        List<Location> route = locationServiceClient.findOptimalRoute(testRouteRequest);

        assertNotNull(route);
        assertEquals(2, route.size());
        assertEquals(testLocation1.getLatitude(), route.get(0).getLatitude());
        assertEquals(testLocation1.getLongitude(), route.get(0).getLongitude());
        assertEquals(testLocation2.getLatitude(), route.get(1).getLatitude());
        assertEquals(testLocation2.getLongitude(), route.get(1).getLongitude());

        verify(locationServiceClient, times(1)).findOptimalRoute(testRouteRequest);
    }

    @Test
    void healthMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = LocationServiceClient.class.getMethod("health");
        assertTrue(method.isAnnotationPresent(GetMapping.class),
                "health method should be annotated with @GetMapping");

        // Test with mock
        when(locationServiceClient.health()).thenReturn("Location Service is up and running!");

        String health = locationServiceClient.health();

        assertNotNull(health);
        assertEquals("Location Service is up and running!", health);

        verify(locationServiceClient, times(1)).health();
    }
}