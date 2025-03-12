package com.mealbroker.restaurant.controller;

import com.mealbroker.restaurant.dto.MenuDTO;
import com.mealbroker.restaurant.dto.MenuItemDTO;
import com.mealbroker.restaurant.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for menu operations
 */
@RestController
@RequestMapping("/api/restaurants")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/branches/{branchId}/menu")
    public ResponseEntity<MenuDTO> getMenu(@PathVariable Long branchId) {
        MenuDTO menu = menuService.getMenu(branchId);
        return ResponseEntity.ok(menu);
    }

    @PostMapping("/branches/{branchId}/menu/items")
    public ResponseEntity<MenuItemDTO> addMenuItem(
            @PathVariable Long branchId,
            @Valid @RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO addedItem = menuService.addMenuItem(branchId, menuItemDTO);
        return new ResponseEntity<>(addedItem, HttpStatus.CREATED);
    }

    @PutMapping("/menu/items/{menuItemId}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @PathVariable Long menuItemId,
            @Valid @RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO updatedItem = menuService.updateMenuItem(menuItemId, menuItemDTO);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/menu/items/{menuItemId}")
    public ResponseEntity<Void> removeMenuItem(@PathVariable Long menuItemId) {
        menuService.removeMenuItem(menuItemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/menu/items/{menuItemId}/availability")
    public ResponseEntity<MenuItemDTO> updateItemAvailability(
            @PathVariable Long menuItemId,
            @RequestParam boolean available) {
        MenuItemDTO updatedItem = menuService.updateItemAvailability(menuItemId, available);
        return ResponseEntity.ok(updatedItem);
    }

    @PatchMapping("/menu/items/{menuItemId}/stock")
    public ResponseEntity<MenuItemDTO> updateItemStock(
            @PathVariable Long menuItemId,
            @RequestParam int stock) {
        MenuItemDTO updatedItem = menuService.updateItemStock(menuItemId, stock);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/branches/{branchId}/menu/available-items")
    public ResponseEntity<List<MenuItemDTO>> getAvailableMenuItems(@PathVariable Long branchId) {
        List<MenuItemDTO> items = menuService.getAvailableMenuItems(branchId);
        return ResponseEntity.ok(items);
    }
}