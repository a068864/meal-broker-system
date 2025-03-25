package com.mealbroker.restaurant.repository;

import com.mealbroker.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Restaurant entity
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Find a restaurant by name
     *
     * @param name the name to search for
     * @return Optional containing the restaurant if found
     */
    Optional<Restaurant> findByName(String name);

    /**
     * Find restaurants by cuisine type
     *
     * @param cuisine the cuisine to search for
     * @return List of restaurants with the given cuisine
     */
    List<Restaurant> findByCuisine(String cuisine);
}