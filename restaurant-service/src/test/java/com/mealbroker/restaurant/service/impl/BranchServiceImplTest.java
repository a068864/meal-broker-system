package com.mealbroker.restaurant.service.impl;

import com.mealbroker.domain.*;
import com.mealbroker.restaurant.dto.BranchDTO;
import com.mealbroker.restaurant.dto.MenuItemDTO;
import com.mealbroker.restaurant.exception.BranchNotFoundException;
import com.mealbroker.restaurant.exception.RestaurantNotFoundException;
import com.mealbroker.restaurant.repository.BranchRepository;
import com.mealbroker.restaurant.repository.MenuItemRepository;
import com.mealbroker.restaurant.repository.MenuRepository;
import com.mealbroker.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private BranchServiceImpl branchService;

    private Restaurant testRestaurant;
    private Branch testBranch;
    private BranchDTO testBranchDTO;
    private Location testLocation;
    private Menu testMenu;
    private MenuItem testMenuItem;
    private List<Branch> branchList;

    @BeforeEach
    void setUp() {
        // Create test data
        testLocation = new Location(40.7128, -74.0060);
        testRestaurant = new Restaurant(1L, "Test Restaurant", "Italian");

        testBranch = new Branch(1L, "Downtown Branch", testLocation);
        testBranch.setRestaurant(testRestaurant);
        testBranch.setActive(true);

        testBranchDTO = new BranchDTO(1L, "Downtown Branch", 1L, testLocation);
        testBranchDTO.setActive(true);

        Branch branch2 = new Branch(2L, "Uptown Branch", testLocation);
        branch2.setRestaurant(testRestaurant);
        branch2.setActive(true);

        branchList = Arrays.asList(testBranch, branch2);

        // Create menu and menu item
        testMenu = new Menu(1L);
        testMenuItem = new MenuItem(1L, "Pizza", "Classic pizza", 10.99);
        testMenuItem.setStock(10);
        testMenuItem.setAvailable(true);
        testMenu.addItem(testMenuItem);

        testBranch.setMenu(testMenu);
    }

    @Test
    void testCreateBranch_Success() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(branchRepository.save(any(Branch.class))).thenReturn(testBranch);

        // When
        BranchDTO result = branchService.createBranch(1L, testBranchDTO);

        // Then
        assertNotNull(result);
        assertEquals(testBranchDTO.getBranchName(), result.getBranchName());
        assertEquals(testBranchDTO.getLocation(), result.getLocation());
        assertEquals(testBranchDTO.isActive(), result.isActive());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    void testCreateBranch_RestaurantNotFound() {
        // Given
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RestaurantNotFoundException.class, () -> {
            branchService.createBranch(99L, testBranchDTO);
        });

        verify(restaurantRepository, times(1)).findById(99L);
        verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    void testGetBranch_Success() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        // When
        BranchDTO result = branchService.getBranch(1L);

        // Then
        assertNotNull(result);
        assertEquals(testBranch.getBranchId(), result.getBranchId());
        assertEquals(testBranch.getBranchName(), result.getBranchName());
        assertEquals(testBranch.getLocation(), result.getLocation());
        assertEquals(testBranch.isActive(), result.isActive());
        verify(branchRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBranch_NotFound() {
        // Given
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BranchNotFoundException.class, () -> {
            branchService.getBranch(99L);
        });

        verify(branchRepository, times(1)).findById(99L);
    }

    @Test
    void testGetBranchesByRestaurant_Success() {
        // Given
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(branchRepository.findByRestaurantRestaurantId(1L)).thenReturn(branchList);

        // When
        List<BranchDTO> results = branchService.getBranchesByRestaurant(1L);

        // Then
        assertNotNull(results);
        assertEquals(branchList.size(), results.size());
        verify(restaurantRepository, times(1)).existsById(1L);
        verify(branchRepository, times(1)).findByRestaurantRestaurantId(1L);
    }

    @Test
    void testGetBranchesByRestaurant_RestaurantNotFound() {
        // Given
        when(restaurantRepository.existsById(99L)).thenReturn(false);

        // When/Then
        assertThrows(RestaurantNotFoundException.class, () -> {
            branchService.getBranchesByRestaurant(99L);
        });

        verify(restaurantRepository, times(1)).existsById(99L);
        verify(branchRepository, never()).findByRestaurantRestaurantId(anyLong());
    }

    @Test
    void testGetActiveBranchesByRestaurant_Success() {
        // Given
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(branchRepository.findByRestaurantRestaurantIdAndActiveTrue(1L)).thenReturn(branchList);

        // When
        List<BranchDTO> results = branchService.getActiveBranchesByRestaurant(1L);

        // Then
        assertNotNull(results);
        assertEquals(branchList.size(), results.size());
        verify(restaurantRepository, times(1)).existsById(1L);
        verify(branchRepository, times(1)).findByRestaurantRestaurantIdAndActiveTrue(1L);
    }

    @Test
    void testUpdateBranch_Success() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));
        when(branchRepository.save(any(Branch.class))).thenReturn(testBranch);

        BranchDTO updateDTO = new BranchDTO(1L, "Updated Branch", 1L, testLocation);
        updateDTO.setActive(false);

        // When
        BranchDTO result = branchService.updateBranch(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(updateDTO.getBranchName(), result.getBranchName());
        assertEquals(updateDTO.isActive(), result.isActive());
        verify(branchRepository, times(1)).findById(1L);
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    void testUpdateBranch_NotFound() {
        // Given
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());

        BranchDTO updateDTO = new BranchDTO(99L, "Updated Branch", 1L, testLocation);

        // When/Then
        assertThrows(BranchNotFoundException.class, () -> {
            branchService.updateBranch(99L, updateDTO);
        });

        verify(branchRepository, times(1)).findById(99L);
        verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    void testDeleteBranch_Success() {
        // Given
        when(branchRepository.existsById(1L)).thenReturn(true);
        doNothing().when(branchRepository).deleteById(1L);

        // When
        branchService.deleteBranch(1L);

        // Then
        verify(branchRepository, times(1)).existsById(1L);
        verify(branchRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteBranch_NotFound() {
        // Given
        when(branchRepository.existsById(99L)).thenReturn(false);

        // When/Then
        assertThrows(BranchNotFoundException.class, () -> {
            branchService.deleteBranch(99L);
        });

        verify(branchRepository, times(1)).existsById(99L);
        verify(branchRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateBranchStatus_Success() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));
        when(branchRepository.save(any(Branch.class))).thenReturn(testBranch);

        // When
        BranchDTO result = branchService.updateBranchStatus(1L, false);

        // Then
        assertNotNull(result);
        assertFalse(result.isActive());
        verify(branchRepository, times(1)).findById(1L);
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    void testCheckItemsAvailability_AllAvailable() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        List<MenuItemDTO> menuItems = List.of(
                new MenuItemDTO(1L, 2) // Menu item ID 1, quantity 2
        );

        // When
        boolean result = branchService.checkItemsAvailability(1L, menuItems);

        // Then
        assertTrue(result);
        verify(branchRepository, times(1)).findById(1L);
    }

    @Test
    void testCheckItemsAvailability_ItemNotAvailable() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        testMenuItem.setAvailable(false);

        List<MenuItemDTO> menuItems = List.of(
                new MenuItemDTO(1L, 1) // Menu item ID 1, quantity 1
        );

        // When
        boolean result = branchService.checkItemsAvailability(1L, menuItems);

        // Then
        assertFalse(result);
        verify(branchRepository, times(1)).findById(1L);
    }

    @Test
    void testCheckItemsAvailability_NotEnoughStock() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        List<MenuItemDTO> menuItems = List.of(
                new MenuItemDTO(1L, 20) // Menu item ID 1, quantity 20 (more than available stock)
        );

        // When
        boolean result = branchService.checkItemsAvailability(1L, menuItems);

        // Then
        assertFalse(result);
        verify(branchRepository, times(1)).findById(1L);
    }

    @Test
    void testCheckItemsAvailability_ItemNotFound() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        List<MenuItemDTO> menuItems = List.of(
                new MenuItemDTO(99L, 1) // Menu item ID 99 (not in menu), quantity 1
        );

        // When
        boolean result = branchService.checkItemsAvailability(1L, menuItems);

        // Then
        assertFalse(result);
        verify(branchRepository, times(1)).findById(1L);
    }

    @Test
    void testCheckItemsAvailability_CreateMenuIfNotExists() {
        // Given
        Branch branchWithoutMenu = new Branch(2L, "New Branch", testLocation);
        branchWithoutMenu.setRestaurant(testRestaurant);
        when(branchRepository.findById(2L)).thenReturn(Optional.of(branchWithoutMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(new Menu(2L));
        when(branchRepository.save(any(Branch.class))).thenReturn(branchWithoutMenu);

        List<MenuItemDTO> menuItems = List.of(
                new MenuItemDTO(1L, 1) // Menu item ID 1, quantity 1
        );

        // When
        boolean result = branchService.checkItemsAvailability(2L, menuItems);

        // Then
        assertFalse(result); // Item not found in newly created menu
        verify(branchRepository, times(1)).findById(2L);
        verify(menuRepository, times(1)).save(any(Menu.class));
        verify(branchRepository, times(1)).save(any(Branch.class));
    }
}