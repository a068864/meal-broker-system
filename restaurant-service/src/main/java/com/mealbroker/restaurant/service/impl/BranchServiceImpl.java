package com.mealbroker.restaurant.service.impl;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Menu;
import com.mealbroker.domain.MenuItem;
import com.mealbroker.domain.Restaurant;
import com.mealbroker.domain.dto.BranchDTO;
import com.mealbroker.domain.dto.MenuItemDTO;
import com.mealbroker.restaurant.exception.BranchNotFoundException;
import com.mealbroker.restaurant.exception.RestaurantNotFoundException;
import com.mealbroker.restaurant.repository.BranchRepository;
import com.mealbroker.restaurant.repository.MenuItemRepository;
import com.mealbroker.restaurant.repository.MenuRepository;
import com.mealbroker.restaurant.repository.RestaurantRepository;
import com.mealbroker.restaurant.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the BranchService interface
 */
@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository,
                             RestaurantRepository restaurantRepository,
                             MenuRepository menuRepository,
                             MenuItemRepository menuItemRepository) {
        this.branchRepository = branchRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    @Transactional
    public BranchDTO createBranch(Long restaurantId, BranchDTO branchDTO) {
        // Get the restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId));

        // Create new branch using the correct constructor
        Branch branch = new Branch(
                branchDTO.getBranchName(),
                branchDTO.getLocation()
        );

        branch.setRestaurant(restaurant);
        branch.setActive(branchDTO.isActive());

        // Save the branch
        Branch savedBranch = branchRepository.save(branch);

        // Convert back to DTO
        return convertToDTO(savedBranch);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchDTO getBranch(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with ID: " + branchId));

        return convertToDTO(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDTO> getBranchesByRestaurant(Long restaurantId) {
        // Verify restaurant exists
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId);
        }

        return branchRepository.findByRestaurantRestaurantId(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDTO> getActiveBranchesByRestaurant(Long restaurantId) {
        // Verify restaurant exists
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId);
        }

        return branchRepository.findByRestaurantRestaurantIdAndActiveTrue(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BranchDTO updateBranch(Long branchId, BranchDTO branchDTO) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with ID: " + branchId));

        // Update branch fields
        branch.setBranchName(branchDTO.getBranchName());
        branch.setLocation(branchDTO.getLocation());
        branch.setActive(branchDTO.isActive());

        // Save updated branch
        Branch updatedBranch = branchRepository.save(branch);

        return convertToDTO(updatedBranch);
    }

    @Override
    @Transactional
    public void deleteBranch(Long branchId) {
        if (!branchRepository.existsById(branchId)) {
            throw new BranchNotFoundException("Branch not found with ID: " + branchId);
        }

        branchRepository.deleteById(branchId);
    }

    @Override
    @Transactional
    public BranchDTO updateBranchStatus(Long branchId, boolean active) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with ID: " + branchId));

        branch.setActive(active);
        Branch updatedBranch = branchRepository.save(branch);

        return convertToDTO(updatedBranch);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkItemsAvailability(Long branchId, List<MenuItemDTO> menuItems) {
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

        // Check each item
        for (MenuItemDTO item : menuItems) {
            MenuItem menuItem = menu.getItem(item.getMenuItemId());

            // If the item doesn't exist, is not available, or doesn't have enough stock
            if (menuItem == null || !menuItem.isAvailable() || menuItem.getStock() < item.getQuantity()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Helper method to convert Branch entity to BranchDTO
     */
    private BranchDTO convertToDTO(Branch branch) {
        BranchDTO branchDTO = new BranchDTO(
                branch.getBranchId(),
                branch.getBranchName(),
                branch.getRestaurantId(),
                branch.getLocation()
        );
        branchDTO.setActive(branch.isActive());

        return branchDTO;
    }
}