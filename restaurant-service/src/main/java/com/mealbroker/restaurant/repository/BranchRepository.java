package com.mealbroker.restaurant.repository;

import com.mealbroker.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Branch entity
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    /**
     * Find branches by restaurant ID
     *
     * @param restaurantId the restaurant ID to search for
     * @return List of branches for the restaurant
     */
    List<Branch> findByRestaurantRestaurantId(Long restaurantId);

    /**
     * Find active branches by restaurant ID
     *
     * @param restaurantId the restaurant ID to search for
     * @return List of active branches for the restaurant
     */
    List<Branch> findByRestaurantRestaurantIdAndActiveTrue(Long restaurantId);

    /**
     * Find branches within a certain distance of a location
     * Note: This is a simplified version that uses a rough approximation
     *
     * @param restaurantId the restaurant ID
     * @param latitude     the latitude of the center point
     * @param longitude    the longitude of the center point
     * @param maxDistance  the maximum distance in degrees (approximate)
     * @return List of branches within the specified distance
     */
    @Query("SELECT b FROM Branch b WHERE b.restaurant.restaurantId = :restaurantId " +
            "AND b.isActive = true " +
            "AND (b.location.latitude - :latitude) * (b.location.latitude - :latitude) + " +
            "(b.location.longitude - :longitude) * (b.location.longitude - :longitude) <= :maxDistance * :maxDistance")
    List<Branch> findNearbyBranches(
            @Param("restaurantId") Long restaurantId,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("maxDistance") double maxDistance);
}