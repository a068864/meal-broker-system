package com.mealbroker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a restaurant chain like McDonald's or KFC
 */
@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @NotBlank(message = "Restaurant name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cuisine")
    private String cuisine;

    // Branches should be fully managed by Restaurant
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Branch> branches = new ArrayList<>();

    // Orders should not be deleted when Restaurant is deleted
    @OneToMany(mappedBy = "restaurant", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Order> orders = new ArrayList<>();

    public Restaurant() {

    }

    public Restaurant(String name, String cuisine) {
        this.name = name;
        this.cuisine = cuisine;
    }

    public Restaurant(Long restaurantId, String name, String cuisine) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.cuisine = cuisine;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        List<Branch> branchesToRemove = new ArrayList<>(this.branches);
        for (Branch branch : branchesToRemove) {
            removeBranch(branch);
        }
        if (branches != null) {
            for (Branch branch : branches) {
                addBranch(branch);
            }
        }
    }

    public void addBranch(Branch branch) {
        if (branch != null && !branches.contains(branch)) {
            branches.add(branch);
            if (branch.getRestaurant() != this) {
                branch.setRestaurant(this);
            }
        }
    }

    public void removeBranch(Branch branch) {
        if (branch != null && branches.remove(branch)) {
            if (branch.getRestaurant() == this) {
                branch.setRestaurant(null);
            }
        }
    }

    public List<Branch> getActiveBranches() {
        return branches.stream()
                .filter(Branch::isActive)
                .collect(Collectors.toList());
    }

    public void addOrder(Order order) {
        if (order != null && !orders.contains(order)) {
            orders.add(order);
            if (order.getRestaurant() != this) {
                order.setRestaurant(this);
            }
        }
    }

    public void removeOrder(Order order) {
        if (order != null && orders.remove(order)) {
            if (order.getRestaurant() == this) {
                order.setRestaurant(null);
            }
        }
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantId=" + restaurantId +
                ", name='" + name + '\'' +
                ", cuisine='" + cuisine + '\'' +
                ", branchCount=" + (branches != null ? branches.size() : 0) +
                '}';
    }
}