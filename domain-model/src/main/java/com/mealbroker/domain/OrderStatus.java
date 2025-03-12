package com.mealbroker.domain;

// Represents the possible states of an order
public enum OrderStatus {
    NEW,
    PROCESSING,
    CONFIRMED,
    IN_PREPARATION,
    READY,
    COMPLETED,
    CANCELLED
}