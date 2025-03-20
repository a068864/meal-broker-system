package com.mealbroker.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.OrderStatus;
import com.mealbroker.domain.dto.OrderDTO;
import com.mealbroker.domain.dto.OrderHistoryDTO;
import com.mealbroker.domain.dto.OrderItemDTO;
import com.mealbroker.domain.dto.OrderStatusUpdateDTO;
import com.mealbroker.order.exception.OrderNotFoundException;
import com.mealbroker.order.exception.OrderStatusException;
import com.mealbroker.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO orderDTO;
    private OrderItemDTO orderItemDTO;
    private Date orderDate;
    private List<OrderHistoryDTO> orderHistoryList;

    @BeforeEach
    void setUp() {
        // Set up test data
        orderDate = new Date();

        orderItemDTO = new OrderItemDTO(101L, 1, 5.99);
        orderItemDTO.setOrderItemId(5L);
        orderItemDTO.setMenuItemName("Big Mac");

        orderDTO = new OrderDTO(1L, 2L);
        orderDTO.setOrderId(4L);
        orderDTO.setBranchId(3L);
        orderDTO.setOrderTime(orderDate);
        orderDTO.setStatus(OrderStatus.NEW);
        orderDTO.setItems(Collections.singletonList(orderItemDTO));
        orderDTO.setTotalAmount(5.99);

        Location customerLocation = new Location(43.6532, -79.3832);
        orderDTO.setCustomerLocation(customerLocation);

        // Set up order history data
        OrderHistoryDTO history1 = new OrderHistoryDTO(1L, 4L, null, OrderStatus.NEW, orderDate, "Order created");
        OrderHistoryDTO history2 = new OrderHistoryDTO(2L, 4L, OrderStatus.NEW, OrderStatus.PROCESSING, new Date(), "Order processing started");
        orderHistoryList = Arrays.asList(history1, history2);
    }

    @Test
    void createOrder_shouldReturnCreatedOrder() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(orderDTO.getOrderId()))
                .andExpect(jsonPath("$.customerId").value(orderDTO.getCustomerId()))
                .andExpect(jsonPath("$.restaurantId").value(orderDTO.getRestaurantId()))
                .andExpect(jsonPath("$.branchId").value(orderDTO.getBranchId()))
                .andExpect(jsonPath("$.status").value(orderDTO.getStatus().toString()))
                .andExpect(jsonPath("$.items[0].menuItemId").value(orderItemDTO.getMenuItemId()));

        verify(orderService, times(1)).createOrder(any(OrderDTO.class));
    }

    @Test
    void getOrder_shouldReturnOrder() throws Exception {
        // Arrange
        when(orderService.getOrder(orderDTO.getOrderId())).thenReturn(orderDTO);

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}", orderDTO.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderDTO.getOrderId()))
                .andExpect(jsonPath("$.customerId").value(orderDTO.getCustomerId()))
                .andExpect(jsonPath("$.restaurantId").value(orderDTO.getRestaurantId()))
                .andExpect(jsonPath("$.branchId").value(orderDTO.getBranchId()))
                .andExpect(jsonPath("$.status").value(orderDTO.getStatus().toString()))
                .andExpect(jsonPath("$.items[0].menuItemId").value(orderItemDTO.getMenuItemId()));

        verify(orderService, times(1)).getOrder(orderDTO.getOrderId());
    }

    @Test
    void getOrder_notFound_shouldReturnNotFound() throws Exception {
        // Arrange
        Long nonExistentId = 999L;
        when(orderService.getOrder(nonExistentId)).thenThrow(new OrderNotFoundException("Order not found"));

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrder(nonExistentId);
    }

    @Test
    void getOrdersByCustomer_shouldReturnOrders() throws Exception {
        // Arrange
        List<OrderDTO> orders = Collections.singletonList(orderDTO);
        when(orderService.getOrdersByCustomer(orderDTO.getCustomerId())).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/customer/{customerId}", orderDTO.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderDTO.getOrderId()))
                .andExpect(jsonPath("$[0].customerId").value(orderDTO.getCustomerId()));

        verify(orderService, times(1)).getOrdersByCustomer(orderDTO.getCustomerId());
    }

    @Test
    void getOrdersByRestaurant_shouldReturnOrders() throws Exception {
        // Arrange
        List<OrderDTO> orders = Collections.singletonList(orderDTO);
        when(orderService.getOrdersByRestaurant(orderDTO.getRestaurantId())).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/restaurant/{restaurantId}", orderDTO.getRestaurantId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderDTO.getOrderId()))
                .andExpect(jsonPath("$[0].restaurantId").value(orderDTO.getRestaurantId()));

        verify(orderService, times(1)).getOrdersByRestaurant(orderDTO.getRestaurantId());
    }

    @Test
    void getOrdersByBranch_shouldReturnOrders() throws Exception {
        // Arrange
        List<OrderDTO> orders = Collections.singletonList(orderDTO);
        when(orderService.getOrdersByBranch(orderDTO.getBranchId())).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/branch/{branchId}", orderDTO.getBranchId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderDTO.getOrderId()))
                .andExpect(jsonPath("$[0].branchId").value(orderDTO.getBranchId()));

        verify(orderService, times(1)).getOrdersByBranch(orderDTO.getBranchId());
    }

    @Test
    void getOrdersByStatus_shouldReturnOrders() throws Exception {
        // Arrange
        List<OrderDTO> orders = Collections.singletonList(orderDTO);
        when(orderService.getOrdersByStatus(OrderStatus.NEW)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/status/{status}", OrderStatus.NEW))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderDTO.getOrderId()))
                .andExpect(jsonPath("$[0].status").value(OrderStatus.NEW.toString()));

        verify(orderService, times(1)).getOrdersByStatus(OrderStatus.NEW);
    }

    @Test
    void getOrdersByBranchAndStatus_shouldReturnOrders() throws Exception {
        // Arrange
        List<OrderDTO> orders = Collections.singletonList(orderDTO);
        when(orderService.getOrdersByBranchAndStatus(orderDTO.getBranchId(), OrderStatus.NEW)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/branch/{branchId}/status/{status}",
                        orderDTO.getBranchId(), OrderStatus.NEW))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderDTO.getOrderId()))
                .andExpect(jsonPath("$[0].branchId").value(orderDTO.getBranchId()))
                .andExpect(jsonPath("$[0].status").value(OrderStatus.NEW.toString()));

        verify(orderService, times(1)).getOrdersByBranchAndStatus(orderDTO.getBranchId(), OrderStatus.NEW);
    }

    @Test
    void getOrdersByCustomerAndStatus_shouldReturnOrders() throws Exception {
        // Arrange
        List<OrderDTO> orders = Collections.singletonList(orderDTO);
        when(orderService.getOrdersByCustomerAndStatus(orderDTO.getCustomerId(), OrderStatus.NEW)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/customer/{customerId}/status/{status}",
                        orderDTO.getCustomerId(), OrderStatus.NEW))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderDTO.getOrderId()))
                .andExpect(jsonPath("$[0].customerId").value(orderDTO.getCustomerId()))
                .andExpect(jsonPath("$[0].status").value(OrderStatus.NEW.toString()));

        verify(orderService, times(1)).getOrdersByCustomerAndStatus(orderDTO.getCustomerId(), OrderStatus.NEW);
    }

    @Test
    void getOrdersByDateRange_shouldReturnOrders() throws Exception {
        // Arrange
        List<OrderDTO> orders = Collections.singletonList(orderDTO);
        Date startDate = new Date(System.currentTimeMillis() - 86400000); // 1 day ago
        Date endDate = new Date(System.currentTimeMillis() + 86400000);   // 1 day in future

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDateString = dateFormat.format(startDate);
        String endDateString = dateFormat.format(endDate);

        when(orderService.getOrdersByDateRange(any(Date.class), any(Date.class))).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/date-range")
                        .param("startDate", startDateString)
                        .param("endDate", endDateString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderDTO.getOrderId()));

        verify(orderService, times(1)).getOrdersByDateRange(any(Date.class), any(Date.class));
    }

    @Test
    void updateOrderStatus_shouldReturnUpdatedOrder() throws Exception {
        // Arrange
        OrderStatusUpdateDTO statusUpdate = new OrderStatusUpdateDTO(OrderStatus.PROCESSING);
        OrderDTO updatedOrder = new OrderDTO(orderDTO.getCustomerId(), orderDTO.getRestaurantId());
        updatedOrder.setOrderId(orderDTO.getOrderId());
        updatedOrder.setStatus(OrderStatus.PROCESSING);
        updatedOrder.setItems(orderDTO.getItems());

        when(orderService.updateOrderStatus(orderDTO.getOrderId(), OrderStatus.PROCESSING)).thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/{orderId}/status", orderDTO.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(updatedOrder.getOrderId()))
                .andExpect(jsonPath("$.status").value(OrderStatus.PROCESSING.toString()));

        verify(orderService, times(1)).updateOrderStatus(orderDTO.getOrderId(), OrderStatus.PROCESSING);
    }

    @Test
    void updateOrderStatus_invalidTransition_shouldReturnBadRequest() throws Exception {
        // Arrange
        OrderStatusUpdateDTO statusUpdate = new OrderStatusUpdateDTO(OrderStatus.PROCESSING);

        when(orderService.updateOrderStatus(orderDTO.getOrderId(), OrderStatus.PROCESSING))
                .thenThrow(new OrderStatusException("Invalid status transition"));

        // Act & Assert
        mockMvc.perform(put("/api/orders/{orderId}/status", orderDTO.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest());

        verify(orderService, times(1)).updateOrderStatus(orderDTO.getOrderId(), OrderStatus.PROCESSING);
    }

    @Test
    void cancelOrder_shouldReturnCancelledOrder() throws Exception {
        // Arrange
        OrderDTO cancelledOrder = new OrderDTO(orderDTO.getCustomerId(), orderDTO.getRestaurantId());
        cancelledOrder.setOrderId(orderDTO.getOrderId());
        cancelledOrder.setStatus(OrderStatus.CANCELLED);
        cancelledOrder.setItems(orderDTO.getItems());

        when(orderService.cancelOrder(orderDTO.getOrderId())).thenReturn(cancelledOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderDTO.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(cancelledOrder.getOrderId()))
                .andExpect(jsonPath("$.status").value(OrderStatus.CANCELLED.toString()));

        verify(orderService, times(1)).cancelOrder(orderDTO.getOrderId());
    }

    @Test
    void cancelOrder_alreadyCompleted_shouldReturnBadRequest() throws Exception {
        // Arrange
        when(orderService.cancelOrder(orderDTO.getOrderId()))
                .thenThrow(new OrderStatusException("Cannot cancel a completed order"));

        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderDTO.getOrderId()))
                .andExpect(status().isBadRequest());

        verify(orderService, times(1)).cancelOrder(orderDTO.getOrderId());
    }

    @Test
    void getOrderHistory_shouldReturnHistoryList() throws Exception {
        // Arrange
        when(orderService.getOrderHistory(orderDTO.getOrderId())).thenReturn(orderHistoryList);

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}/history", orderDTO.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].historyId").value(orderHistoryList.get(0).getHistoryId()))
                .andExpect(jsonPath("$[0].orderId").value(orderHistoryList.get(0).getOrderId()))
                .andExpect(jsonPath("$[0].newStatus").value(orderHistoryList.get(0).getNewStatus().toString()))
                .andExpect(jsonPath("$[0].notes").value(orderHistoryList.get(0).getNotes()))
                .andExpect(jsonPath("$[1].historyId").value(orderHistoryList.get(1).getHistoryId()))
                .andExpect(jsonPath("$[1].previousStatus").value(orderHistoryList.get(1).getPreviousStatus().toString()));

        verify(orderService, times(1)).getOrderHistory(orderDTO.getOrderId());
    }

    @Test
    void getOrderHistory_orderNotFound_shouldReturnNotFound() throws Exception {
        // Arrange
        Long nonExistentId = 999L;
        when(orderService.getOrderHistory(nonExistentId))
                .thenThrow(new OrderNotFoundException("Order not found"));

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}/history", nonExistentId))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderHistory(nonExistentId);
    }
}