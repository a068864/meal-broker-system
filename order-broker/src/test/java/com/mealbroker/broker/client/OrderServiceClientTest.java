package com.mealbroker.broker.client;

import com.mealbroker.domain.OrderStatus;
import com.mealbroker.domain.dto.OrderCreateRequestDTO;
import com.mealbroker.domain.dto.OrderResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceClientTest {

    @Mock
    private OrderServiceClient orderServiceClient;

    private OrderCreateRequestDTO testOrderCreateRequest;
    private OrderResponseDTO testOrderResponse;

    @BeforeEach
    void setUp() {
        // Ensure that the interface has required annotations
        assertTrue(OrderServiceClient.class.isAnnotationPresent(FeignClient.class),
                "OrderServiceClient should be annotated with @FeignClient");

        FeignClient feignClient = OrderServiceClient.class.getAnnotation(FeignClient.class);
        assertEquals("order-service", feignClient.name(),
                "FeignClient name should be 'order-service'");

        // Create test data
        testOrderCreateRequest = new OrderCreateRequestDTO();
        testOrderCreateRequest.setCustomerId(1L);
        testOrderCreateRequest.setRestaurantId(1L);
        testOrderCreateRequest.setBranchId(1L);
        testOrderCreateRequest.setItems(new ArrayList<>());

        testOrderResponse = new OrderResponseDTO();
        testOrderResponse.setOrderId(1L);
        testOrderResponse.setCustomerId(1L);
        testOrderResponse.setRestaurantId(1L);
        testOrderResponse.setBranchId(1L);
        testOrderResponse.setStatus(OrderStatus.NEW);
        testOrderResponse.setOrderTime(new Date());
    }

    @Test
    void createOrderMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = OrderServiceClient.class.getMethod("createOrder", OrderCreateRequestDTO.class);
        assertTrue(method.isAnnotationPresent(PostMapping.class),
                "createOrder method should be annotated with @PostMapping");

        // Test with mock
        when(orderServiceClient.createOrder(any(OrderCreateRequestDTO.class))).thenReturn(testOrderResponse);

        OrderResponseDTO response = orderServiceClient.createOrder(testOrderCreateRequest);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(1L, response.getCustomerId());
        assertEquals(1L, response.getRestaurantId());
        assertEquals(1L, response.getBranchId());
        assertEquals(OrderStatus.NEW, response.getStatus());

        verify(orderServiceClient, times(1)).createOrder(testOrderCreateRequest);
    }

    @Test
    void getOrderMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = OrderServiceClient.class.getMethod("getOrder", Long.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class),
                "getOrder method should be annotated with @GetMapping");

        // Test with mock
        when(orderServiceClient.getOrder(1L)).thenReturn(testOrderResponse);

        OrderResponseDTO response = orderServiceClient.getOrder(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.NEW, response.getStatus());

        verify(orderServiceClient, times(1)).getOrder(1L);
    }

    @Test
    void updateOrderStatusMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = OrderServiceClient.class.getMethod("updateOrderStatus", Long.class, OrderStatus.class);
        assertTrue(method.isAnnotationPresent(PutMapping.class),
                "updateOrderStatus method should be annotated with @PutMapping");

        // Create expected response
        OrderResponseDTO updatedResponse = new OrderResponseDTO();
        updatedResponse.setOrderId(1L);
        updatedResponse.setCustomerId(1L);
        updatedResponse.setRestaurantId(1L);
        updatedResponse.setBranchId(1L);
        updatedResponse.setStatus(OrderStatus.CONFIRMED);
        updatedResponse.setOrderTime(new Date());

        // Test with mock
        when(orderServiceClient.updateOrderStatus(eq(1L), eq(OrderStatus.CONFIRMED))).thenReturn(updatedResponse);

        OrderResponseDTO response = orderServiceClient.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());

        verify(orderServiceClient, times(1)).updateOrderStatus(1L, OrderStatus.CONFIRMED);
    }

    @Test
    void cancelOrderMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = OrderServiceClient.class.getMethod("cancelOrder", Long.class);
        assertTrue(method.isAnnotationPresent(PostMapping.class),
                "cancelOrder method should be annotated with @PostMapping");

        // Create expected response
        OrderResponseDTO cancelledResponse = new OrderResponseDTO();
        cancelledResponse.setOrderId(1L);
        cancelledResponse.setCustomerId(1L);
        cancelledResponse.setRestaurantId(1L);
        cancelledResponse.setBranchId(1L);
        cancelledResponse.setStatus(OrderStatus.CANCELLED);
        cancelledResponse.setOrderTime(new Date());

        // Test with mock
        when(orderServiceClient.cancelOrder(1L)).thenReturn(cancelledResponse);

        OrderResponseDTO response = orderServiceClient.cancelOrder(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.CANCELLED, response.getStatus());

        verify(orderServiceClient, times(1)).cancelOrder(1L);
    }
}