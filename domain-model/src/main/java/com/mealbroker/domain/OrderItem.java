package com.mealbroker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item in a customer's order
 */
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull(message = "Menu item ID is required")
    @Column(name = "menu_item_id", nullable = false)
    private Long menuItemId;

    @Column(name = "menu_item_name")
    private String menuItemName;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Min(value = 0, message = "Price cannot be negative")
    @Column(name = "price", nullable = false)
    private double price;

    @Min(value = 0, message = "Additional charges cannot be negative")
    @Column(name = "additional_charges")
    private double additionalCharges;

    @ElementCollection
    @CollectionTable(name = "order_item_instructions", joinColumns = @JoinColumn(name = "order_item_id"))
    @Column(name = "instruction")
    private List<String> specialInstructions = new ArrayList<>();

    /**
     * Default constructor required by JPA
     */
    public OrderItem() {
        this.quantity = 1;
        this.price = 0.0;
        this.additionalCharges = 0.0;
    }

    /**
     * Create an order item with ID and quantity
     *
     * @param orderItemId the order item ID
     * @param quantity    the quantity
     */
    public OrderItem(Long orderItemId, int quantity) {
        this();
        this.orderItemId = orderItemId;
        this.quantity = Math.max(1, quantity);
    }

    /**
     * Create an order item with menu item ID, quantity, and price
     *
     * @param menuItemId the menu item ID
     * @param quantity   the quantity
     * @param price      the price
     */
    public OrderItem(Long menuItemId, int quantity, double price) {
        this();
        this.menuItemId = menuItemId;
        this.quantity = Math.max(1, quantity);
        this.price = Math.max(0.0, price);
    }

    /**
     * Create an order item with menu item ID, name, quantity, and price
     *
     * @param menuItemId   the menu item ID
     * @param menuItemName the menu item name
     * @param quantity     the quantity
     * @param price        the price
     */
    public OrderItem(Long menuItemId, String menuItemName, int quantity, double price) {
        this();
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.quantity = Math.max(1, quantity);
        this.price = Math.max(0.0, price);
    }

    /**
     * Create an order item with order item ID, menu item ID, name, quantity, and price
     *
     * @param orderItemId  the order item ID
     * @param menuItemId   the menu item ID
     * @param menuItemName the menu item name
     * @param quantity     the quantity
     * @param price        the price
     */
    public OrderItem(Long orderItemId, Long menuItemId, String menuItemName, int quantity, double price) {
        this();
        this.orderItemId = orderItemId;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.quantity = Math.max(1, quantity);
        this.price = Math.max(0.0, price);
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = Math.max(0.0, price);
    }

    public double getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(double additionalCharges) {
        this.additionalCharges = Math.max(0.0, additionalCharges);
    }

    /**
     * Calculate total price including quantity and additional charges
     *
     * @return total price
     */
    public double getTotalPrice() {
        return price * quantity + additionalCharges;
    }

    public List<String> getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(List<String> specialInstructions) {
        this.specialInstructions = specialInstructions != null ? specialInstructions : new ArrayList<>();
    }

    /**
     * Add a special instruction to this order item
     *
     * @param instruction the instruction to add
     */
    public void addSpecialInstruction(String instruction) {
        if (instruction != null && !instruction.trim().isEmpty()) {
            specialInstructions.add(instruction);
        }
    }

    /**
     * Remove a special instruction from this order item
     *
     * @param instruction the instruction to remove
     */
    public void removeSpecialInstruction(String instruction) {
        specialInstructions.remove(instruction);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", menuItemId=" + menuItemId +
                ", menuItemName='" + menuItemName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", additionalCharges=" + additionalCharges +
                '}';
    }
}