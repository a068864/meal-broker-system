package com.mealbroker.restaurant.repository;

import com.mealbroker.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for MenuItem entity
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Find menu items by menu ID
     *
     * @param menuId the menu ID to search for
     * @return List of menu items for the menu
     */
    List<MenuItem> findByMenuMenuId(Long menuId);

    /**
     * Find available menu items by menu ID
     *
     * @param menuId the menu ID to search for
     * @return List of available menu items for the menu
     */
    List<MenuItem> findByMenuMenuIdAndIsAvailableTrue(Long menuId);
}