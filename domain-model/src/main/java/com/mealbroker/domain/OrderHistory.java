package com.mealbroker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "order_history")
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    @Column(name = "previous_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus previousStatus;

    @NotNull
    @Column(name = "new_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus newStatus;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(name = "notes", length = 500)
    private String notes;

    public OrderHistory() {
        this.timestamp = new Date();
    }

    public OrderHistory(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        this();
        this.order = order;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public OrderHistory(Order order, OrderStatus previousStatus, OrderStatus newStatus, String notes) {
        this(order, previousStatus, newStatus);
        this.notes = notes;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(OrderStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "OrderHistory{" +
                "historyId=" + historyId +
                ", orderId=" + (order != null ? order.getOrderId() : null) +
                ", previousStatus=" + previousStatus +
                ", newStatus=" + newStatus +
                ", timestamp=" + timestamp +
                '}';
    }

}