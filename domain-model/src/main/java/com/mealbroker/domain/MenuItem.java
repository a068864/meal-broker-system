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

    public MenuItem() {
    }

    public MenuItem(String name, String description, double price) {
        this.name = name;
        setDescription(description);
        setPrice(price);
    }

    public MenuItem(Long menuItemId, String name, String description, double price) {
        this.menuItemId = menuItemId;
        this.name = name;
        setDescription(description);
        setPrice(price);
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
        this.price = price;
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
        this.stock = stock;
    }

    public boolean reduceStock(int quantity) {
        if (quantity <= 0) {
            return true;
        }
        if (stock < quantity) {
            stock = 0;
            this.isAvailable = false;
            return false;
        } else {
            this.stock -= quantity;
            if (this.stock == 0) {
                this.isAvailable = false;
            }
        }
        return true;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        if (allergens == null) {
            allergens = new ArrayList<>();
        }
        this.allergens = allergens;
    }


    public void addAllergen(String allergen) {
        allergens.add(allergen);
    }

    public void removeAllergen(String allergen) {
        allergens.remove(allergen);
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        if (this.menu != menu) {
            Menu oldMenu = this.menu;
            this.menu = menu;
            if (oldMenu != null && oldMenu.getItems().contains(this)) {
                oldMenu.removeItem(this);
            }
            if (menu != null && !menu.getItems().contains(this)) {
                menu.addItem(this);
            }
        }
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