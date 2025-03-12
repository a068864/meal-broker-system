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

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Branch> branches = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    /**
     * Default constructor required by JPA
     */
    public Restaurant() {
    }

    /**
     * Create a new restaurant with name and cuisine
     *
     * @param name    the restaurant name
     * @param cuisine the restaurant cuisine type
     */
    public Restaurant(String name, String cuisine) {
        this.name = name;
        this.cuisine = cuisine;
    }

    /**
     * Create a new restaurant with ID, name and cuisine
     *
     * @param restaurantId the restaurant ID
     * @param name         the restaurant name
     * @param cuisine      the restaurant cuisine type
     */
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
        // Clear existing branches
        this.branches.clear();

        // Add all new branches and set relationship
        if (branches != null) {
            for (Branch branch : branches) {
                addBranch(branch);
            }
        }
    }

    /**
     * Add a branch to this restaurant and establish bidirectional relationship
     *
     * @param branch the branch to add
     */
    public void addBranch(Branch branch) {
        branches.add(branch);
        branch.setRestaurant(this);
    }

    /**
     * Remove a branch from this restaurant
     *
     * @param branch the branch to remove
     */
    public void removeBranch(Branch branch) {
        branches.remove(branch);
        branch.setRestaurant(null);
    }

    /**
     * Get all active branches for this restaurant
     *
     * @return list of active branches
     */
    public List<Branch> getActiveBranches() {
        return branches.stream()
                .filter(Branch::isActive)
                .collect(Collectors.toList());
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