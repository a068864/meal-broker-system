package com.mealbroker.restaurant.service.impl;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Menu;
import com.mealbroker.domain.MenuItem;
import com.mealbroker.domain.dto.MenuDTO;
import com.mealbroker.domain.dto.MenuItemDTO;
import com.mealbroker.restaurant.exception.BranchNotFoundException;
import com.mealbroker.restaurant.exception.MenuItemNotFoundException;
import com.mealbroker.restaurant.repository.BranchRepository;
import com.mealbroker.restaurant.repository.MenuItemRepository;
import com.mealbroker.restaurant.repository.MenuRepository;
import com.mealbroker.restaurant.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the MenuService interface
 */
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final BranchRepository branchRepository;

    @Autowired
    public MenuServiceImpl(MenuRepository menuRepository,
                           MenuItemRepository menuItemRepository,
                           BranchRepository branchRepository) {
        this.menuRepository = menuRepository;
        this.menuItemRepository = menuItemRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MenuDTO getMenu(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with ID: " + branchId));

        Menu menu = branch.getMenu();
        if (menu == null) {
            // Create a menu if it doesn't exist
            menu = new Menu();
            menu.setBranch(branch);
            menu = menuRepository.save(menu);

            branch.setMenu(menu);
            branchRepository.save(branch);
        }

        return convertToDTO(menu);
    }

    @Override
    @Transactional
    public MenuItemDTO addMenuItem(Long branchId, MenuItemDTO menuItemDTO) {
        // Get the branch and its menu
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with ID: " + branchId));

        Menu menu = branch.getMenu();
        if (menu == null) {
            // Create a menu if it doesn't exist
            menu = new Menu();
            menu.setBranch(branch);
            menu = menuRepository.save(menu);

            branch.setMenu(menu);
            branchRepository.save(branch);
        }

        // Create new menu item
        MenuItem menuItem = new MenuItem(
                menuItemDTO.getName(),
                menuItemDTO.getDescription(),
                menuItemDTO.getPrice()
        );
        menuItem.setAvailable(menuItemDTO.isAvailable());
        menuItem.setStock(menuItemDTO.getStock());

        // Add allergens if provided
        if (menuItemDTO.getAllergens() != null) {
            menuItemDTO.getAllergens().forEach(menuItem::addAllergen);
        }

        // Add to menu
        menu.addItem(menuItem);
        menuItem.setMenu(menu);

        // Save the menu item
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        return convertToDTO(savedMenuItem);
    }

    @Override
    @Transactional
    public MenuItemDTO updateMenuItem(Long menuItemId, MenuItemDTO menuItemDTO) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with ID: " + menuItemId));

        // Update menu item fields
        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setPrice(menuItemDTO.getPrice());
        menuItem.setAvailable(menuItemDTO.isAvailable());
        menuItem.setStock(menuItemDTO.getStock());

        // Update allergens
        if (menuItemDTO.getAllergens() != null) {
            // Clear existing allergens
            menuItem.getAllergens().clear();
            // Add new allergens
            menuItemDTO.getAllergens().forEach(menuItem::addAllergen);
        }

        // Save updated menu item
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        return convertToDTO(updatedMenuItem);
    }

    @Override
    @Transactional
    public void removeMenuItem(Long menuItemId) {
        if (!menuItemRepository.existsById(menuItemId)) {
            throw new MenuItemNotFoundException("Menu item not found with ID: " + menuItemId);
        }

        menuItemRepository.deleteById(menuItemId);
    }

    @Override
    @Transactional
    public MenuItemDTO updateItemAvailability(Long menuItemId, boolean available) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with ID: " + menuItemId));

        menuItem.setAvailable(available);
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        return convertToDTO(updatedMenuItem);
    }

    @Override
    @Transactional
    public MenuItemDTO updateItemStock(Long menuItemId, int stock) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with ID: " + menuItemId));

        menuItem.setStock(stock);
        // If stock is 0, automatically set availability to false
        if (stock <= 0) {
            menuItem.setAvailable(false);
        }

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        return convertToDTO(updatedMenuItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getMenuItems(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with ID: " + branchId));

        Menu menu = branch.getMenu();
        if (menu == null) {
            return List.of();
        }

        return menuItemRepository.findByMenuMenuId(menu.getMenuId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getAvailableMenuItems(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with ID: " + branchId));

        Menu menu = branch.getMenu();
        if (menu == null) {
            return List.of();
        }

        return menuItemRepository.findByMenuMenuIdAndIsAvailableTrue(menu.getMenuId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to convert Menu entity to MenuDTO
     */
    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO menuDTO = new MenuDTO(menu.getMenuId());

        // Convert menu items
        List<MenuItemDTO> menuItemDTOs = menu.getItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        menuDTO.setItems(menuItemDTOs);

        return menuDTO;
    }

    /**
     * Helper method to convert MenuItem entity to MenuItemDTO
     */
    private MenuItemDTO convertToDTO(MenuItem menuItem) {
        MenuItemDTO menuItemDTO = new MenuItemDTO(
                menuItem.getMenuItemId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice()
        );

        menuItemDTO.setAvailable(menuItem.isAvailable());
        menuItemDTO.setStock(menuItem.getStock());
        menuItemDTO.setAllergens(menuItem.getAllergens());

        return menuItemDTO;
    }
}