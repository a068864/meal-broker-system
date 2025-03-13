package com.mealbroker.restaurant.service.impl;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.Menu;
import com.mealbroker.domain.MenuItem;
import com.mealbroker.restaurant.dto.MenuDTO;
import com.mealbroker.restaurant.dto.MenuItemDTO;
import com.mealbroker.restaurant.exception.BranchNotFoundException;
import com.mealbroker.restaurant.exception.MenuItemNotFoundException;
import com.mealbroker.restaurant.repository.BranchRepository;
import com.mealbroker.restaurant.repository.MenuItemRepository;
import com.mealbroker.restaurant.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    private Branch testBranch;
    private Menu testMenu;
    private MenuItem testMenuItem;
    private MenuItemDTO testMenuItemDTO;
    private List<MenuItem> menuItemList;

    @BeforeEach
    void setUp() {
        // Create test data
        Location location = new Location(40.7128, -74.0060);
        testBranch = new Branch(1L, "Downtown Branch", location);

        testMenu = new Menu(1L);
        testBranch.setMenu(testMenu);

        testMenuItem = new MenuItem(1L, "Pizza", "Delicious pizza", 10.99);
        testMenuItem.setAvailable(true);
        testMenuItem.setStock(10);
        testMenu.addItem(testMenuItem);

        MenuItem item2 = new MenuItem(2L, "Burger", "Tasty burger", 8.99);
        item2.setAvailable(true);
        item2.setStock(5);
        testMenu.addItem(item2);

        menuItemList = Arrays.asList(testMenuItem, item2);

        // Create DTOs
        testMenuItemDTO = new MenuItemDTO(1L, "Pizza", "Delicious pizza", 10.99);
        testMenuItemDTO.setAvailable(true);
        testMenuItemDTO.setStock(10);
    }

    @Test
    void testGetMenu_Success() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        // When
        MenuDTO result = menuService.getMenu(1L);

        // Then
        assertNotNull(result);
        assertEquals(testMenu.getMenuId(), result.getMenuId());
        assertEquals(menuItemList.size(), result.getItems().size());
        verify(branchRepository, times(1)).findById(1L);
    }

    @Test
    void testGetMenu_BranchNotFound() {
        // Given
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BranchNotFoundException.class, () -> {
            menuService.getMenu(99L);
        });

        verify(branchRepository, times(1)).findById(99L);
    }

    @Test
    void testGetMenu_CreateMenuIfNotExists() {
        // Given
        Branch branchWithoutMenu = new Branch(2L, "New Branch", new Location(40.7128, -74.0060));
        when(branchRepository.findById(2L)).thenReturn(Optional.of(branchWithoutMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(new Menu(2L));

        // When
        MenuDTO result = menuService.getMenu(2L);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getMenuId());
        assertTrue(result.getItems().isEmpty());
        verify(branchRepository, times(1)).findById(2L);
        verify(menuRepository, times(1)).save(any(Menu.class));
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    void testAddMenuItem_Success() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(testMenuItem);

        // When
        MenuItemDTO result = menuService.addMenuItem(1L, testMenuItemDTO);

        // Then
        assertNotNull(result);
        assertEquals(testMenuItem.getMenuItemId(), result.getMenuItemId());
        assertEquals(testMenuItem.getName(), result.getName());
        assertEquals(testMenuItem.getPrice(), result.getPrice());
        verify(branchRepository, times(1)).findById(1L);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testAddMenuItem_BranchNotFound() {
        // Given
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BranchNotFoundException.class, () -> {
            menuService.addMenuItem(99L, testMenuItemDTO);
        });

        verify(branchRepository, times(1)).findById(99L);
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void testAddMenuItem_CreateMenuIfNotExists() {
        // Given
        Branch branchWithoutMenu = new Branch(2L, "New Branch", new Location(40.7128, -74.0060));
        when(branchRepository.findById(2L)).thenReturn(Optional.of(branchWithoutMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(new Menu(2L));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(testMenuItem);

        // When
        MenuItemDTO result = menuService.addMenuItem(2L, testMenuItemDTO);

        // Then
        assertNotNull(result);
        assertEquals(testMenuItem.getMenuItemId(), result.getMenuItemId());
        verify(branchRepository, times(1)).findById(2L);
        verify(menuRepository, times(1)).save(any(Menu.class));
        verify(branchRepository, times(1)).save(any(Branch.class));
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testUpdateMenuItem_Success() {
        // Given
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(testMenuItem);

        MenuItemDTO updateDTO = new MenuItemDTO(1L, "Updated Pizza", "Updated description", 12.99);
        updateDTO.setAvailable(false);
        updateDTO.setStock(5);
        List<String> allergens = new ArrayList<>();
        allergens.add("Gluten");
        updateDTO.setAllergens(allergens);

        // When
        MenuItemDTO result = menuService.updateMenuItem(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(updateDTO.getName(), result.getName());
        assertEquals(updateDTO.getDescription(), result.getDescription());
        assertEquals(updateDTO.getPrice(), result.getPrice());
        assertEquals(updateDTO.isAvailable(), result.isAvailable());
        assertEquals(updateDTO.getStock(), result.getStock());
        verify(menuItemRepository, times(1)).findById(1L);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testUpdateMenuItem_NotFound() {
        // Given
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        MenuItemDTO updateDTO = new MenuItemDTO(99L, "Updated Pizza", "Updated description", 12.99);

        // When/Then
        assertThrows(MenuItemNotFoundException.class, () -> {
            menuService.updateMenuItem(99L, updateDTO);
        });

        verify(menuItemRepository, times(1)).findById(99L);
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void testRemoveMenuItem_Success() {
        // Given
        when(menuItemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(menuItemRepository).deleteById(1L);

        // When
        menuService.removeMenuItem(1L);

        // Then
        verify(menuItemRepository, times(1)).existsById(1L);
        verify(menuItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void testRemoveMenuItem_NotFound() {
        // Given
        when(menuItemRepository.existsById(99L)).thenReturn(false);

        // When/Then
        assertThrows(MenuItemNotFoundException.class, () -> {
            menuService.removeMenuItem(99L);
        });

        verify(menuItemRepository, times(1)).existsById(99L);
        verify(menuItemRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateItemAvailability_Success() {
        // Given
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(testMenuItem);

        // When
        MenuItemDTO result = menuService.updateItemAvailability(1L, false);

        // Then
        assertNotNull(result);
        assertFalse(result.isAvailable());
        verify(menuItemRepository, times(1)).findById(1L);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testUpdateItemStock_Success() {
        // Given
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(testMenuItem);

        // When
        MenuItemDTO result = menuService.updateItemStock(1L, 20);

        // Then
        assertNotNull(result);
        assertEquals(20, result.getStock());
        verify(menuItemRepository, times(1)).findById(1L);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testUpdateItemStock_SetUnavailableWhenZero() {
        // Given
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(testMenuItem);

        // When
        MenuItemDTO result = menuService.updateItemStock(1L, 0);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getStock());
        assertFalse(result.isAvailable());
        verify(menuItemRepository, times(1)).findById(1L);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testGetAvailableMenuItems_Success() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));
        when(menuItemRepository.findByMenuMenuIdAndIsAvailableTrue(1L)).thenReturn(menuItemList);

        // When
        List<MenuItemDTO> results = menuService.getAvailableMenuItems(1L);

        // Then
        assertNotNull(results);
        assertEquals(menuItemList.size(), results.size());
        verify(branchRepository, times(1)).findById(1L);
        verify(menuItemRepository, times(1)).findByMenuMenuIdAndIsAvailableTrue(1L);
    }

    @Test
    void testGetAvailableMenuItems_BranchNotFound() {
        // Given
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BranchNotFoundException.class, () -> {
            menuService.getAvailableMenuItems(99L);
        });

        verify(branchRepository, times(1)).findById(99L);
        verify(menuItemRepository, never()).findByMenuMenuIdAndIsAvailableTrue(anyLong());
    }

    @Test
    void testGetAvailableMenuItems_NoMenu() {
        // Given
        Branch branchWithoutMenu = new Branch(2L, "New Branch", new Location(40.7128, -74.0060));
        when(branchRepository.findById(2L)).thenReturn(Optional.of(branchWithoutMenu));

        // When
        List<MenuItemDTO> results = menuService.getAvailableMenuItems(2L);

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(branchRepository, times(1)).findById(2L);
        verify(menuItemRepository, never()).findByMenuMenuIdAndIsAvailableTrue(anyLong());
    }
}