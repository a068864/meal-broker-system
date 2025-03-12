package com.mealbroker.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a menu for a restaurant branch
 */
@Entity
@Table(name = "menus")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "menu")
    private Branch branch;

    /**
     * Default constructor required by JPA
     */
    public Menu() {
    }

    /**
     * Create a new menu with a specific ID
     *
     * @param menuId the menu ID
     */
    public Menu(Long menuId) {
        this.menuId = menuId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        // Clear existing items
        this.items.clear();

        // Add all new items and set bidirectional relationship
        if (items != null) {
            for (MenuItem item : items) {
                addItem(item);
            }
        }
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    /**
     * Add a menu item to this menu and establish bidirectional relationship
     *
     * @param item the item to add
     */
    public void addItem(MenuItem item) {
        items.add(item);
        item.setMenu(this);
    }

    /**
     * Remove a menu item from this menu
     *
     * @param item the item to remove
     */
    public void removeItem(MenuItem item) {
        items.remove(item);
        item.setMenu(null);
    }

    /**
     * Get a specific menu item by ID
     *
     * @param itemId the item ID to find
     * @return the menu item if found, null otherwise
     */
    public MenuItem getItem(Long itemId) {
        for (MenuItem item : items) {
            if (item.getMenuItemId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Update the availability of a specific menu item
     *
     * @param itemId    the ID of the item to update
     * @param available the new availability status
     */
    public void updateItemAvailability(Long itemId, boolean available) {
        MenuItem item = getItem(itemId);
        if (item != null) {
            item.setAvailable(available);
        }
    }

    /**
     * Get list of all available menu items
     *
     * @return list of available items
     */
    public List<MenuItem> getAvailableItems() {
        List<MenuItem> availableItems = new ArrayList<>();
        for (MenuItem item : items) {
            if (item.isAvailable()) {
                availableItems.add(item);
            }
        }
        return availableItems;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "menuId=" + menuId +
                ", itemCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}