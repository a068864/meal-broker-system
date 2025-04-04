package com.mealbroker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain object representing a restaurant branch
 */
@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long branchId;

    @NotBlank(message = "Branch name is required")
    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @NotNull(message = "Branch location is required")
    @Embedded
    private Location location;

    @Column(name = "address")
    private String address;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "operating_radius")
    private Integer operatingRadius = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    // Menu is fully managed by Branch
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    // Orders should not be deleted when Branch is deleted
    @OneToMany(mappedBy = "branch", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Order> orders = new ArrayList<>();

    public Branch() {
    }

    public Branch(String branchName, Location location) {
        this.branchName = branchName;
        this.location = location;
    }

    public Branch(Long branchId, String branchName, Location location) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.location = location;
    }

    // Getters and Setters
    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getRestaurantId() {
        return restaurant != null ? restaurant.getRestaurantId() : null;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active != null ? active : true; // Default to true if null
    }

    public Integer getOperatingRadius() {
        return operatingRadius;
    }

    public void setOperatingRadius(Integer operatingRadius) {
        this.operatingRadius = operatingRadius;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void setMenu(Menu menu) {
        if (this.menu != menu) {
            Menu oldMenu = this.menu;
            this.menu = menu;
            if (oldMenu != null && oldMenu.getBranch() == this) {
                oldMenu.setBranch(null);
            }
            if (menu != null && menu.getBranch() != this) {
                menu.setBranch(this);
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
            if (oldRestaurant != null && oldRestaurant.getBranches().contains(this)) {
                oldRestaurant.removeBranch(this);
            }
            if (restaurant != null && !restaurant.getBranches().contains(this)) {
                restaurant.addBranch(this);
            }
        }
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order) {
        if (!orders.contains(order)) {
            orders.add(order);
            if (order.getBranch() != this) {
                order.setBranch(this);
            }
        }
    }

    public void removeOrder(Order order) {
        if (orders.remove(order) && order.getBranch() == this) {
            order.setBranch(null);
        }
    }

    @Override
    public String toString() {
        return "Branch{" +
                "branchId=" + branchId +
                ", branchName='" + branchName + '\'' +
                ", address='" + address + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}