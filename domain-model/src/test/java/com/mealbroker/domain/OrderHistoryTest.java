package com.mealbroker.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class OrderHistoryTest {
    private Order testOrder;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private Date testDate;
    private String testNotes;

    @BeforeEach
    void setUp() {
        // Create a test order
        Customer customer = new Customer();
        customer.setCustomerId(1L);

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(2L);

        testOrder = new Order(customer, restaurant);
        testOrder.setOrderId(3L);

        // Set up test data
        previousStatus = OrderStatus.NEW;
        newStatus = OrderStatus.PROCESSING;
        testDate = new Date();
        testNotes = "Status changed due to kitchen starting preparation";
    }

    @Test
    void testDefaultConstructor() {
        // When
        OrderHistory history = new OrderHistory();

        // Then
        assertNotNull(history);
        assertNotNull(history.getTimestamp(), "Timestamp should be initialized");
        assertTrue(System.currentTimeMillis() - history.getTimestamp().getTime() < 1000,
                "Timestamp should be set to current time");
        assertNull(history.getOrder());
        assertNull(history.getPreviousStatus());
        assertNull(history.getNewStatus());
        assertNull(history.getNotes());
    }

    @Test
    void testConstructorWithoutNotes() {
        // When
        OrderHistory history = new OrderHistory(testOrder, previousStatus, newStatus);

        // Then
        assertNotNull(history);
        assertEquals(testOrder, history.getOrder());
        assertEquals(previousStatus, history.getPreviousStatus());
        assertEquals(newStatus, history.getNewStatus());
        assertNotNull(history.getTimestamp());
        assertNull(history.getNotes());
    }

    @Test
    void testConstructorWithNotes() {
        // When
        OrderHistory history = new OrderHistory(testOrder, previousStatus, newStatus, testNotes);

        // Then
        assertNotNull(history);
        assertEquals(testOrder, history.getOrder());
        assertEquals(previousStatus, history.getPreviousStatus());
        assertEquals(newStatus, history.getNewStatus());
        assertNotNull(history.getTimestamp());
        assertEquals(testNotes, history.getNotes());
    }

    @Test
    void testSetAndGetHistoryId() {
        // Given
        OrderHistory history = new OrderHistory();
        Long historyId = 101L;

        // When
        history.setHistoryId(historyId);

        // Then
        assertEquals(historyId, history.getHistoryId());
    }

    @Test
    void testSetAndGetOrder() {
        // Given
        OrderHistory history = new OrderHistory();

        // When
        history.setOrder(testOrder);

        // Then
        assertEquals(testOrder, history.getOrder());
    }

    @Test
    void testSetAndGetPreviousStatus() {
        // Given
        OrderHistory history = new OrderHistory();

        // When
        history.setPreviousStatus(previousStatus);

        // Then
        assertEquals(previousStatus, history.getPreviousStatus());
    }

    @Test
    void testSetAndGetNewStatus() {
        // Given
        OrderHistory history = new OrderHistory();

        // When
        history.setNewStatus(newStatus);

        // Then
        assertEquals(newStatus, history.getNewStatus());
    }

    @Test
    void testSetAndGetTimestamp() {
        // Given
        OrderHistory history = new OrderHistory();

        // When
        history.setTimestamp(testDate);

        // Then
        assertEquals(testDate, history.getTimestamp());
    }

    @Test
    void testSetAndGetNotes() {
        // Given
        OrderHistory history = new OrderHistory();

        // When
        history.setNotes(testNotes);

        // Then
        assertEquals(testNotes, history.getNotes());
    }

    @Test
    void testToString() {
        // Given
        OrderHistory history = new OrderHistory(testOrder, previousStatus, newStatus);
        history.setHistoryId(101L);

        // When
        String result = history.toString();

        // Then
        assertTrue(result.contains("historyId=101"));
        assertTrue(result.contains("orderId=3"));
        assertTrue(result.contains("previousStatus=" + previousStatus));
        assertTrue(result.contains("newStatus=" + newStatus));
    }

    @Test
    void testToStringWithNullOrder() {
        // Given
        OrderHistory history = new OrderHistory(null, previousStatus, newStatus);

        // When
        String result = history.toString();

        // Then
        assertTrue(result.contains("orderId=null"));
    }
}