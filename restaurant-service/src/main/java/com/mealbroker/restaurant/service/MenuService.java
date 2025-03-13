package com.mealbroker.restaurant.service;

import com.mealbroker.domain.dto.MenuDTO;
import com.mealbroker.domain.dto.MenuItemDTO;

import java.util.List;

/**
 * Service interface for menu operations
 */
public interface MenuService {

    /**
     * Get menu for a branch
     *
     * @param branchId the branch ID
     * @return the menu
     */
    MenuDTO getMenu(Long branchId);

    /**
     * Add an item to a menu
     *
     * @param branchId    the branch ID
     * @param menuItemDTO the menu item data
     * @return the added menu item
     */
    MenuItemDTO addMenuItem(Long branchId, MenuItemDTO menuItemDTO);

    /**
     * Update a menu item
     *
     * @param menuItemId  the menu item ID
     * @param menuItemDTO the updated menu item data
     * @return the updated menu item
     */
    MenuItemDTO updateMenuItem(Long menuItemId, MenuItemDTO menuItemDTO);

    /**
     * Remove a menu item
     *
     * @param menuItemId the menu item ID
     */
    void removeMenuItem(Long menuItemId);

    /**
     * Update item availability
     *
     * @param menuItemId the menu item ID
     * @param available  the new availability status
     * @return the updated menu item
     */
    MenuItemDTO updateItemAvailability(Long menuItemId, boolean available);

    /**
     * Update item stock
     *
     * @param menuItemId the menu item ID
     * @param stock      the new stock level
     * @return the updated menu item
     */
    MenuItemDTO updateItemStock(Long menuItemId, int stock);

    /**
     * Get available menu items for a branch
     *
     * @param branchId the branch ID
     * @return list of available menu items
     */
    List<MenuItemDTO> getAvailableMenuItems(Long branchId);
}