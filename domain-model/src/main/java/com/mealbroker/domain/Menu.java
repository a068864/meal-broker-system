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

    // Menu items are fully managed by Menu
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> items = new ArrayList<>();

    // Branch should not be deleted when Menu is deleted
    @OneToOne(mappedBy = "menu")
    private Branch branch;

    public Menu() {
    }

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
        List<MenuItem> itemsToRemove = new ArrayList<>(this.items);
        for (MenuItem item : itemsToRemove) {
            if (item.getMenu() == this) {
                item.setMenu(null);
            }
        }
        this.items.clear();
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
        if (this.branch != branch) {
            Branch oldBranch = this.branch;
            this.branch = branch;
            if (oldBranch != null && oldBranch.getMenu() == this) {
                oldBranch.setMenu(null);
            }
            if (branch != null && branch.getMenu() != this) {
                branch.setMenu(this);
            }
        }
    }

    public void addItem(MenuItem item) {
        if (!items.contains(item)) {
            items.add(item);
            if (item.getMenu() != this) {
                item.setMenu(this);
            }
        }
    }

    public void removeItem(MenuItem item) {
        if (items.remove(item) && item.getMenu() == this) {
            item.setMenu(null);
        }
    }

    public MenuItem getItem(Long itemId) {
        for (MenuItem item : items) {
            if (item.getMenuItemId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }


    public void updateItemAvailability(Long itemId, boolean available) {
        MenuItem item = getItem(itemId);
        if (item != null) {
            item.setAvailable(available);
        }
    }

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