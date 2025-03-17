package com.mealbroker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer who places orders
 */
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be 10-15 digits with optional + prefix")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Embedded
    private Location location;

    @OneToMany(mappedBy = "customer", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<Order> orders = new ArrayList<>();

    /**
     * Default constructor required by JPA
     */
    public Customer() {
    }

    /**
     * Create a new customer with basic information
     *
     * @param name        the customer's name
     * @param email       the customer's email
     * @param phoneNumber the customer's phone number
     */
    public Customer(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Create a new customer with ID and basic information
     *
     * @param customerId  the customer ID
     * @param name        the customer's name
     * @param email       the customer's email
     * @param phoneNumber the customer's phone number
     */
    public Customer(Long customerId, String name, String email, String phoneNumber) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * Add an order to this customer and establish bidirectional relationship
     *
     * @param order the order to add
     */
    public void addOrder(Order order) {
        if (!orders.contains(order)) {
            orders.add(order);
            order.setCustomer(this);
        }
    }

    /**
     * Remove an order from this customer
     *
     * @param order the order to remove
     */
    public void removeOrder(Order order) {
        if (orders.contains(order)) {
            orders.remove(order);
            order.setCustomer(null);
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}