package com.mealbroker.restaurant.service;

import com.mealbroker.domain.dto.RestaurantDTO;

import java.util.List;

/**
 * Service interface for restaurant operations
 */
public interface RestaurantService {

    /**
     * Create a new restaurant
     *
     * @param restaurantDTO the restaurant data
     * @return the created restaurant
     */
    RestaurantDTO createRestaurant(RestaurantDTO restaurantDTO);

    /**
     * Get a restaurant by ID
     *
     * @param restaurantId the restaurant ID
     * @return the restaurant if found
     */
    RestaurantDTO getRestaurant(Long restaurantId);

    /**
     * Get all restaurants
     *
     * @return list of all restaurants
     */
    List<RestaurantDTO> getAllRestaurants();

    /**
     * Get restaurants by cuisine
     *
     * @param cuisine the cuisine to search for
     * @return list of restaurants with the given cuisine
     */
    List<RestaurantDTO> getRestaurantsByCuisine(String cuisine);

    /**
     * Update a restaurant
     *
     * @param restaurantId  the restaurant ID
     * @param restaurantDTO the updated restaurant data
     * @return the updated restaurant
     */
    RestaurantDTO updateRestaurant(Long restaurantId, RestaurantDTO restaurantDTO);

    /**
     * Delete a restaurant
     *
     * @param restaurantId the restaurant ID
     */
    void deleteRestaurant(Long restaurantId);
}