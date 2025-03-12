package com.mealbroker.restaurant.service;

import com.mealbroker.domain.Location;
import com.mealbroker.restaurant.dto.BranchDTO;
import com.mealbroker.restaurant.dto.MenuItemDTO;

import java.util.List;

/**
 * Service interface for branch operations
 */
public interface BranchService {

    /**
     * Create a new branch for a restaurant
     *
     * @param restaurantId the restaurant ID
     * @param branchDTO    the branch data
     * @return the created branch
     */
    BranchDTO createBranch(Long restaurantId, BranchDTO branchDTO);

    /**
     * Get a branch by ID
     *
     * @param branchId the branch ID
     * @return the branch if found
     */
    BranchDTO getBranch(Long branchId);

    /**
     * Get all branches for a restaurant
     *
     * @param restaurantId the restaurant ID
     * @return list of branches for the restaurant
     */
    List<BranchDTO> getBranchesByRestaurant(Long restaurantId);

    /**
     * Get active branches for a restaurant
     *
     * @param restaurantId the restaurant ID
     * @return list of active branches for the restaurant
     */
    List<BranchDTO> getActiveBranchesByRestaurant(Long restaurantId);

    /**
     * Find nearby branches for a restaurant
     *
     * @param restaurantId the restaurant ID
     * @param location     the customer location
     * @param maxDistance  the maximum distance in kilometers
     * @return list of nearby branches
     */
    List<BranchDTO> findNearbyBranches(Long restaurantId, Location location, double maxDistance);

    /**
     * Update a branch
     *
     * @param branchId  the branch ID
     * @param branchDTO the updated branch data
     * @return the updated branch
     */
    BranchDTO updateBranch(Long branchId, BranchDTO branchDTO);

    /**
     * Delete a branch
     *
     * @param branchId the branch ID
     */
    void deleteBranch(Long branchId);

    /**
     * Update branch status (active/inactive)
     *
     * @param branchId the branch ID
     * @param active   the new active status
     * @return the updated branch
     */
    BranchDTO updateBranchStatus(Long branchId, boolean active);

    /**
     * Check availability of items at a branch
     *
     * @param branchId    the branch ID
     * @param menuItemIds list of menu item IDs and quantities
     * @return true if all items are available in sufficient quantity
     */
    boolean checkItemsAvailability(Long branchId, List<MenuItemDTO> menuItemIds);
}