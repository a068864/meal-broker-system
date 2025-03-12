package com.mealbroker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specific food item on a menu
 */
@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_item_id")
    private Long menuItemId;

    @NotBlank(message = "Item name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Min(value = 0, message = "Price must be a positive value")
    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @Min(value = 0, message = "Stock cannot be negative")
    @Column(name = "stock")
    private int stock = 100;

    @ElementCollection
    @CollectionTable(name = "menu_item_allergens", joinColumns = @JoinColumn(name = "menu_item_id"))
    @Column(name = "allergen")
    private List<String> allergens = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    /**
     * Default constructor required by JPA
     */
    public MenuItem() {
    }

    /**
     * Create a new menu item with basic information
     *
     * @param name        the item name
     * @param description the item description
     * @param price       the item price
     */
    public MenuItem(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    /**
     * Create a new menu item with ID and basic information
     *
     * @param menuItemId  the item ID
     * @param name        the item name
     * @param description the item description
     * @param price       the item price
     */
    public MenuItem(Long menuItemId, String name, String description, double price) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = Math.max(0, price);
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = Math.max(0, stock);
    }

    /**
     * Reduce the item stock by a specified quantity
     * Updates availability if stock becomes zero
     *
     * @param quantity the quantity to reduce by
     */
    public void reduceStock(int quantity) {
        int newStock = Math.max(0, this.stock - quantity);
        boolean stockChanged = (this.stock != newStock);
        this.stock = newStock;
        if (stockChanged && this.stock <= 0) {
            this.isAvailable = false;
        }
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }

    /**
     * Add an allergen to this menu item
     *
     * @param allergen the allergen to add
     */
    public void addAllergen(String allergen) {
        allergens.add(allergen);
    }

    /**
     * Remove an allergen from this menu item
     *
     * @param allergen the allergen to remove
     */
    public void removeAllergen(String allergen) {
        allergens.remove(allergen);
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "menuItemId=" + menuItemId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                ", stock=" + stock +
                '}';
    }
}