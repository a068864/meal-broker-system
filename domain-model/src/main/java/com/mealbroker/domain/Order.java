package com.mealbroker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a customer order
 */
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Restaurant is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @NotNull(message = "Order time is required")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_time", nullable = false)
    private Date orderTime;

    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Embedded
    private Location customerLocation;

    /**
     * Default constructor - initializes with current time and NEW status
     */
    public Order() {
        this.orderTime = new Date();
        this.status = OrderStatus.NEW;
    }

    /**
     * Create a new order for a customer and restaurant
     *
     * @param customer   the ordering customer
     * @param restaurant the selected restaurant
     */
    public Order(Customer customer, Restaurant restaurant) {
        this();
        this.customer = customer;
        this.restaurant = restaurant;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Long getRestaurantId() {
        return restaurant != null ? restaurant.getRestaurantId() : null;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Long getBranchId() {
        return branch != null ? branch.getBranchId() : null;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        // Clear existing items
        this.items.clear();

        // Add all new items and set bidirectional relationship
        if (items != null) {
            for (OrderItem item : items) {
                addItem(item);
            }
        }
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Update order status with validation of allowed transitions
     *
     * @param newStatus the new status to set
     * @throws IllegalStateException if the status transition is invalid
     */
    public void setStatus(OrderStatus newStatus) {
        if (status == null || status == OrderStatus.NEW) {
            status = newStatus;
            return;
        }

        // Validate transitions
        if (isValidStatusTransition(status, newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalStateException("Invalid status transition from " + this.status + " to " + newStatus);
        }
    }

    /**
     * Check if a status transition is valid
     *
     * @param current current status
     * @param next    proposed next status
     * @return true if transition is allowed
     */
    private boolean isValidStatusTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case NEW:
                return next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED;
            case PROCESSING:
                return next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED:
                return next == OrderStatus.IN_PREPARATION || next == OrderStatus.CANCELLED;
            case IN_PREPARATION:
                return next == OrderStatus.READY || next == OrderStatus.CANCELLED;
            case READY:
                return next == OrderStatus.COMPLETED || next == OrderStatus.CANCELLED;
            case COMPLETED:
                return false; // Terminal state
            case CANCELLED:
                return false; // Terminal state
            default:
                return false;
        }
    }

    public Location getCustomerLocation() {
        return customerLocation;
    }

    public void setCustomerLocation(Location customerLocation) {
        this.customerLocation = customerLocation;
    }

    /**
     * Calculate total price of all order items
     *
     * @return total order price
     */
    public double calculateTotal() {
        double total = 0.0;
        if (orderTime != null) {
            for (OrderItem item : items) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        return total;
    }

    /**
     * Add an item to this order and establish bidirectional relationship
     *
     * @param item the item to add
     * @return the added item
     */
    public OrderItem addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        return item;
    }

    /**
     * Remove an item from this order
     *
     * @param item the item to remove
     * @return true if removed, false otherwise
     */
    public boolean removeItem(OrderItem item) {
        boolean removed = items.remove(item);
        item.setOrder(null);
        return removed;
    }

    /**
     * Remove an item from this order by ID
     *
     * @param orderItemId the ID of the item to remove
     * @return true if removed, false otherwise
     */
    public boolean removeItemById(Long orderItemId) {
        for (Iterator<OrderItem> iterator = items.iterator(); iterator.hasNext(); ) {
            OrderItem item = iterator.next();
            if (item.getOrderItemId() != null && item.getOrderItemId().equals(orderItemId)) {
                iterator.remove();
                item.setOrder(null);
                return true;
            }
        }
        return false;
    }

    /**
     * Get an order item by its ID
     *
     * @param orderItemId the item ID to find
     * @return the order item if found, null otherwise
     */
    public OrderItem getItemById(Long orderItemId) {
        for (OrderItem item : items) {
            if (item.getOrderItemId() != null && item.getOrderItemId().equals(orderItemId)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customer=" + (customer != null ? customer.getCustomerId() : null) +
                ", restaurant=" + (restaurant != null ? restaurant.getRestaurantId() : null) +
                ", branch=" + (branch != null ? branch.getBranchId() : null) +
                ", status=" + status +
                ", itemCount=" + (items != null ? items.size() : 0) +
                ", orderTime=" + orderTime +
                '}';
    }
}