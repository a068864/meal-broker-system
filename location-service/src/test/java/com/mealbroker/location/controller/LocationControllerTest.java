package com.mealbroker.location.controller;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.LocationRequestDTO;
import com.mealbroker.domain.dto.NearbyBranchesRequestDTO;
import com.mealbroker.domain.dto.NearestBranchRequestDTO;
import com.mealbroker.domain.dto.RouteRequestDTO;
import com.mealbroker.location.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private Location toronto;
    private Location vancouver;
    private List<Branch> branches;
    private Branch torontoBranch;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        toronto = new Location(43.6532, -79.3832);
        vancouver = new Location(49.2827, -123.1207);

        branches = new ArrayList<>();

        torontoBranch = new Branch("Downtown Toronto", toronto);
        torontoBranch.setBranchId(1L);
        torontoBranch.setActive(true);

        Branch vancouverBranch = new Branch("Downtown Vancouver", vancouver);
        vancouverBranch.setBranchId(2L);
        vancouverBranch.setActive(true);

        branches.add(torontoBranch);
        branches.add(vancouverBranch);
    }

    @Test
    void calculateDistance_shouldReturnCorrectResponse() {
        // Arrange
        LocationRequestDTO requestDTO = new LocationRequestDTO(toronto, vancouver);
        double expectedDistance = 3340.0;

        when(locationService.calculateDistance(toronto, vancouver)).thenReturn(expectedDistance);

        // Act
        Double actualDistance = locationController.calculateDistance(requestDTO);

        // Assert
        assertEquals(expectedDistance, actualDistance);
        verify(locationService, times(1)).calculateDistance(toronto, vancouver);
    }

    @Test
    void findNearestBranch_shouldReturnBranch() {
        // Arrange
        NearestBranchRequestDTO requestDTO = new NearestBranchRequestDTO(toronto, branches, true);

        when(locationService.findNearestBranch(requestDTO)).thenReturn(torontoBranch);

        // Act
        Branch result = locationController.findNearestBranch(requestDTO);

        // Assert
        assertEquals(torontoBranch, result);
        verify(locationService, times(1)).findNearestBranch(requestDTO);
    }

    @Test
    void findNearestBranch_whenNoBranchFound_shouldReturnNull() {
        // Arrange
        NearestBranchRequestDTO requestDTO = new NearestBranchRequestDTO(toronto, branches, true);

        when(locationService.findNearestBranch(requestDTO)).thenReturn(null);

        // Act
        Branch result = locationController.findNearestBranch(requestDTO);

        // Assert
        assertEquals(null, result);
        verify(locationService, times(1)).findNearestBranch(requestDTO);
    }

    @Test
    void findNearbyBranches_shouldReturnBranches() {
        // Arrange
        NearbyBranchesRequestDTO requestDTO = new NearbyBranchesRequestDTO(toronto, branches, 10.0);
        List<Branch> expectedBranches = Collections.singletonList(torontoBranch);

        when(locationService.findNearbyBranches(branches, toronto, 10.0)).thenReturn(expectedBranches);

        // Act
        List<Branch> result = locationController.findNearbyBranches(requestDTO);

        // Assert
        assertEquals(expectedBranches, result);
        verify(locationService, times(1)).findNearbyBranches(branches, toronto, 10.0);
    }

    @Test
    void geocodeAddress_shouldReturnLocation() {
        // Arrange
        String address = "toronto";

        when(locationService.geocodeAddress(address)).thenReturn(toronto);

        // Act
        Location result = locationController.geocodeAddress(address);

        // Assert
        assertEquals(toronto, result);
        verify(locationService, times(1)).geocodeAddress(address);
    }

    @Test
    void reverseGeocode_shouldReturnAddress() {
        // Arrange
        String expectedAddress = "Toronto, ON, Canada";

        when(locationService.reverseGeocode(toronto)).thenReturn(expectedAddress);

        // Act
        String result = locationController.reverseGeocode(toronto);

        // Assert
        assertEquals(expectedAddress, result);
        verify(locationService, times(1)).reverseGeocode(toronto);
    }

    @Test
    void findOptimalRoute_shouldReturnRoute() {
        // Arrange
        Location start = toronto;
        List<Location> destinations = Arrays.asList(vancouver);
        RouteRequestDTO requestDTO = new RouteRequestDTO(start, destinations);
        List<Location> expectedRoute = Arrays.asList(toronto, vancouver);

        when(locationService.findOptimalRoute(start, destinations)).thenReturn(expectedRoute);

        // Act
        List<Location> result = locationController.findOptimalRoute(requestDTO);

        // Assert
        assertEquals(expectedRoute, result);
        verify(locationService, times(1)).findOptimalRoute(start, destinations);
    }

    @Test
    void health_shouldReturnOkResponse() {
        // Act
        ResponseEntity<String> response = locationController.health();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Location Service is up and running!", response.getBody());
    }
}