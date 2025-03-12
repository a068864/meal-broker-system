package com.mealbroker.broker.client;

import com.mealbroker.broker.dto.MenuItemDTO;
import com.mealbroker.domain.Branch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Feign client for the Restaurant Service
 */
@FeignClient(name = "restaurant-service")
public interface RestaurantServiceClient {

    /**
     * Get all branches for a restaurant
     *
     * @param restaurantId the restaurant ID
     * @return list of all branches
     */
    @GetMapping("/api/restaurants/{restaurantId}/branches")
    List<Branch> getBranchesByRestaurant(@PathVariable Long restaurantId);

    /**
     * Check availability of items at a branch
     *
     * @param branchId the branch ID
     * @param items    list of menu items and quantities
     * @return true if all items are available in sufficient quantity
     */
    @PostMapping("/api/restaurants/branches/{branchId}/check-availability")
    boolean checkItemsAvailability(
            @PathVariable Long branchId,
            @RequestBody List<MenuItemDTO> items);
}