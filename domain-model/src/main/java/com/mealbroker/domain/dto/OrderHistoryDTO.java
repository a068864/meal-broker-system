package com.mealbroker.domain.dto;

import com.mealbroker.domain.OrderStatus;

import java.util.Date;

public class OrderHistoryDTO {

    private Long historyId;
    private Long orderId;
    private OrderStatus previousSatus;
    private OrderStatus newStatus;
    private Date timestamp;
    private String notes;

    public OrderHistoryDTO() {
    }

    public OrderHistoryDTO(Long historyId, Long orderId, OrderStatus previousSatus, OrderStatus newStatus, Date timestamp, String notes) {
        this.historyId = historyId;
        this.orderId = orderId;
        this.previousSatus = previousSatus;
        this.newStatus = newStatus;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getPreviousStatus() {
        return previousSatus;
    }

    public Object getNewStatus() {
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

}
