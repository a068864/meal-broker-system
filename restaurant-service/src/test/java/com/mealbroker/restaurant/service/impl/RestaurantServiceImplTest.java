package com.mealbroker.restaurant.service.impl;

import com.mealbroker.domain.Location;
import com.mealbroker.domain.Restaurant;
import com.mealbroker.domain.dto.BranchDTO;
import com.mealbroker.domain.dto.RestaurantDTO;
import com.mealbroker.restaurant.exception.RestaurantNotFoundException;
import com.mealbroker.restaurant.repository.RestaurantRepository;
import com.mealbroker.restaurant.service.BranchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private BranchService branchService;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant testRestaurant;
    private RestaurantDTO testRestaurantDTO;
    private List<Restaurant> restaurantList;
    private List<BranchDTO> branchDTOList;

    @BeforeEach
    void setUp() {
        // Create test data
        testRestaurant = new Restaurant(1L, "Test Restaurant", "Italian");

        testRestaurantDTO = new RestaurantDTO(1L, "Test Restaurant", "Italian");

        restaurantList = new ArrayList<>();
        restaurantList.add(testRestaurant);
        restaurantList.add(new Restaurant(2L, "Another Restaurant", "Chinese"));

        // Create branch DTOs
        Location location = new Location(40.7128, -74.0060);
        BranchDTO branchDTO1 = new BranchDTO(1L, "Downtown Branch", 1L, location);
        BranchDTO branchDTO2 = new BranchDTO(2L, "Uptown Branch", 1L, location);
        branchDTOList = Arrays.asList(branchDTO1, branchDTO2);
    }

    @Test
    void testCreateRestaurant_Success() {
        // Given
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // When
        RestaurantDTO result = restaurantService.createRestaurant(testRestaurantDTO);

        // Then
        assertNotNull(result);
        assertEquals(testRestaurant.getRestaurantId(), result.getRestaurantId());
        assertEquals(testRestaurant.getName(), result.getName());
        assertEquals(testRestaurant.getCuisine(), result.getCuisine());
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void testCreateRestaurantWithBranches_Success() {
        // Given
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);
        when(branchService.createBranch(eq(1L), any(BranchDTO.class))).thenReturn(branchDTOList.get(0));

        testRestaurantDTO.setBranches(branchDTOList);

        // When
        RestaurantDTO result = restaurantService.createRestaurant(testRestaurantDTO);

        // Then
        assertNotNull(result);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
        verify(branchService, times(branchDTOList.size())).createBranch(eq(1L), any(BranchDTO.class));
    }

    @Test
    void testGetRestaurant_Success() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(branchService.getBranchesByRestaurant(1L)).thenReturn(branchDTOList);

        // When
        RestaurantDTO result = restaurantService.getRestaurant(1L);

        // Then
        assertNotNull(result);
        assertEquals(testRestaurant.getRestaurantId(), result.getRestaurantId());
        assertEquals(testRestaurant.getName(), result.getName());
        assertEquals(testRestaurant.getCuisine(), result.getCuisine());
        assertEquals(branchDTOList.size(), result.getBranches().size());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(branchService, times(1)).getBranchesByRestaurant(1L);
    }

    @Test
    void testGetRestaurant_NotFound() {
        // Given
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RestaurantNotFoundException.class, () -> {
            restaurantService.getRestaurant(99L);
        });

        verify(restaurantRepository, times(1)).findById(99L);
        verify(branchService, never()).getBranchesByRestaurant(anyLong());
    }

    @Test
    void testGetAllRestaurants_Success() {
        // Given
        when(restaurantRepository.findAll()).thenReturn(restaurantList);
        when(branchService.getBranchesByRestaurant(anyLong())).thenReturn(branchDTOList);

        // When
        List<RestaurantDTO> results = restaurantService.getAllRestaurants();

        // Then
        assertNotNull(results);
        assertEquals(restaurantList.size(), results.size());
        assertEquals(branchDTOList.size(), results.get(0).getBranches().size());
        verify(restaurantRepository, times(1)).findAll();
        verify(branchService, times(restaurantList.size())).getBranchesByRestaurant(anyLong());
    }

    @Test
    void testGetRestaurantsByCuisine_Success() {
        // Given
        String cuisine = "Italian";
        when(restaurantRepository.findByCuisine(cuisine)).thenReturn(List.of(testRestaurant));
        when(branchService.getBranchesByRestaurant(anyLong())).thenReturn(branchDTOList);

        // When
        List<RestaurantDTO> results = restaurantService.getRestaurantsByCuisine(cuisine);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(cuisine, results.get(0).getCuisine());
        assertEquals(branchDTOList.size(), results.get(0).getBranches().size());
        verify(restaurantRepository, times(1)).findByCuisine(cuisine);
        verify(branchService, times(1)).getBranchesByRestaurant(anyLong());
    }

    @Test
    void testUpdateRestaurant_Success() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        RestaurantDTO updateDTO = new RestaurantDTO(1L, "Updated Restaurant", "French");

        // When
        RestaurantDTO result = restaurantService.updateRestaurant(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(updateDTO.getName(), result.getName());
        assertEquals(updateDTO.getCuisine(), result.getCuisine());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void testUpdateRestaurant_NotFound() {
        // Given
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        RestaurantDTO updateDTO = new RestaurantDTO(99L, "Updated Restaurant", "French");

        // When/Then
        assertThrows(RestaurantNotFoundException.class, () -> {
            restaurantService.updateRestaurant(99L, updateDTO);
        });

        verify(restaurantRepository, times(1)).findById(99L);
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void testDeleteRestaurant_Success() {
        // Given
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(branchService.getBranchesByRestaurant(1L)).thenReturn(branchDTOList);
        doNothing().when(restaurantRepository).deleteById(1L);
        doNothing().when(branchService).deleteBranch(anyLong());

        // When
        restaurantService.deleteRestaurant(1L);

        // Then
        verify(restaurantRepository, times(1)).existsById(1L);
        verify(branchService, times(1)).getBranchesByRestaurant(1L);
        verify(branchService, times(branchDTOList.size())).deleteBranch(anyLong());
        verify(restaurantRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteRestaurant_NotFound() {
        // Given
        when(restaurantRepository.existsById(99L)).thenReturn(false);

        // When/Then
        assertThrows(RestaurantNotFoundException.class, () -> {
            restaurantService.deleteRestaurant(99L);
        });

        verify(restaurantRepository, times(1)).existsById(99L);
        verify(branchService, never()).getBranchesByRestaurant(anyLong());
        verify(branchService, never()).deleteBranch(anyLong());
        verify(restaurantRepository, never()).deleteById(anyLong());
    }
}