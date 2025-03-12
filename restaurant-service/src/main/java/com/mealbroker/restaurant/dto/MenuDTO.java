package com.mealbroker.restaurant.dto;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Menu entities
 */
public class MenuDTO {

    private Long menuId;

    @Valid
    private List<MenuItemDTO> items = new ArrayList<>();

    // Constructors
    public MenuDTO() {
    }

    public MenuDTO(Long menuId) {
        this.menuId = menuId;
    }

    // Getters and Setters
    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public List<MenuItemDTO> getItems() {
        return items;
    }

    public void setItems(List<MenuItemDTO> items) {
        this.items = items;
    }
}
