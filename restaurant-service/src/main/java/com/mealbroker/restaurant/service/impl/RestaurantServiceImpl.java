package com.mealbroker.restaurant.service.impl;

import com.mealbroker.domain.Restaurant;
import com.mealbroker.restaurant.dto.BranchDTO;
import com.mealbroker.restaurant.dto.RestaurantDTO;
import com.mealbroker.restaurant.exception.RestaurantNotFoundException;
import com.mealbroker.restaurant.repository.RestaurantRepository;
import com.mealbroker.restaurant.service.BranchService;
import com.mealbroker.restaurant.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the RestaurantService interface
 */
@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final BranchService branchService;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, BranchService branchService) {
        this.restaurantRepository = restaurantRepository;
        this.branchService = branchService;
    }

    @Override
    @Transactional
    public RestaurantDTO createRestaurant(RestaurantDTO restaurantDTO) {
        // Convert DTO to entity
        Restaurant restaurant = new Restaurant(
                restaurantDTO.getName(),
                restaurantDTO.getCuisine()
        );

        // Save the restaurant
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        // Convert back to DTO
        RestaurantDTO savedRestaurantDTO = convertToDTO(savedRestaurant);

        // Create branches if provided
        if (restaurantDTO.getBranches() != null && !restaurantDTO.getBranches().isEmpty()) {
            List<BranchDTO> savedBranches = restaurantDTO.getBranches().stream()
                    .map(branchDTO -> branchService.createBranch(savedRestaurant.getRestaurantId(), branchDTO))
                    .collect(Collectors.toList());

            savedRestaurantDTO.setBranches(savedBranches);
        }

        return savedRestaurantDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDTO getRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId));

        RestaurantDTO restaurantDTO = convertToDTO(restaurant);

        // Load branches
        List<BranchDTO> branches = branchService.getBranchesByRestaurant(restaurantId);
        restaurantDTO.setBranches(branches);

        return restaurantDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(restaurant -> {
                    RestaurantDTO dto = convertToDTO(restaurant);
                    // Load branches for each restaurant
                    List<BranchDTO> branches = branchService.getBranchesByRestaurant(restaurant.getRestaurantId());
                    dto.setBranches(branches);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDTO> getRestaurantsByCuisine(String cuisine) {
        return restaurantRepository.findByCuisine(cuisine).stream()
                .map(restaurant -> {
                    RestaurantDTO dto = convertToDTO(restaurant);
                    // Load branches for each restaurant
                    List<BranchDTO> branches = branchService.getBranchesByRestaurant(restaurant.getRestaurantId());
                    dto.setBranches(branches);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RestaurantDTO updateRestaurant(Long restaurantId, RestaurantDTO restaurantDTO) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId));

        // Update restaurant fields
        restaurant.setName(restaurantDTO.getName());
        restaurant.setCuisine(restaurantDTO.getCuisine());

        // Save updated restaurant
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        return convertToDTO(updatedRestaurant);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId);
        }

        // Get all branches for this restaurant
        List<BranchDTO> branches = branchService.getBranchesByRestaurant(restaurantId);

        // Delete all branches
        branches.forEach(branch -> branchService.deleteBranch(branch.getBranchId()));

        // Delete restaurant
        restaurantRepository.deleteById(restaurantId);
    }

    /**
     * Helper method to convert Restaurant entity to RestaurantDTO
     */
    private RestaurantDTO convertToDTO(Restaurant restaurant) {
        return new RestaurantDTO(
                restaurant.getRestaurantId(),
                restaurant.getName(),
                restaurant.getCuisine()
        );
    }
}