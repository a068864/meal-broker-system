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

    // Customer, Restaurant, Branch should not be deleted when Order is deleted
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

    // Order Items are fully managed by Order
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

    public Order() {
        this.orderTime = new Date();
        this.status = OrderStatus.NEW;
    }

    public Order(Customer customer, Restaurant restaurant) {
        this();
        setCustomer(customer);
        setRestaurant(restaurant);
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
        if (this.customer != customer) {
            Customer oldCustomer = this.customer;
            this.customer = customer;
            if (oldCustomer != null && oldCustomer.getOrders().contains(this)) {
                oldCustomer.removeOrder(this);
            }
            if (customer != null && !customer.getOrders().contains(this)) {
                customer.addOrder(this);
            }
        }
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        if (this.restaurant != restaurant) {
            Restaurant oldRestaurant = this.restaurant;
            this.restaurant = restaurant;
            if (oldRestaurant != null && oldRestaurant.getOrders().contains(this)) {
                oldRestaurant.getOrders().remove(this);
            }
            if (restaurant != null && !restaurant.getOrders().contains(this)) {
                restaurant.getOrders().add(this);
            }
        }
    }

    public Long getRestaurantId() {
        return restaurant != null ? restaurant.getRestaurantId() : null;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        if (this.branch != branch) {
            Branch oldBranch = this.branch;
            this.branch = branch;
            if (oldBranch != null && oldBranch.getOrders().contains(this)) {
                oldBranch.removeOrder(this);
            }
            if (branch != null && !branch.getOrders().contains(this)) {
                branch.addOrder(this);
            }
        }
    }

    public Long getBranchId() {
        return branch != null ? branch.getBranchId() : null;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items.clear();
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

    @Transient
    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getPrice() * item.getQuantity();
            if (item.getAdditionalCharges() > 0.0) {
                total += item.getAdditionalCharges();
            }
        }
        return total;
    }

    public OrderItem addItem(OrderItem item) {
        if (item != null && !items.contains(item)) {
            items.add(item);
            if (item.getOrder() != this) {
                item.setOrder(this);
            }
        }
        return item;
    }

    public boolean removeItem(OrderItem item) {
        if (item != null && items.remove(item)) {
            if (item.getOrder() == this) {
                item.setOrder(null);
            }
            return true;
        }
        return false;
    }

    public boolean removeItemById(Long orderItemId) {
        for (Iterator<OrderItem> iterator = items.iterator(); iterator.hasNext(); ) {
            OrderItem item = iterator.next();
            if (item.getOrderItemId() != null && item.getOrderItemId().equals(orderItemId)) {
                iterator.remove();
                if (item.getOrder() == this) {
                    item.setOrder(null);
                }
                return true;
            }
        }
        return false;
    }

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