package com.mealbroker.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealbroker.broker.client.CustomerServiceClient;
import com.mealbroker.broker.client.LocationServiceClient;
import com.mealbroker.broker.client.OrderServiceClient;
import com.mealbroker.broker.client.RestaurantServiceClient;
import com.mealbroker.broker.service.OrderBrokerService;
import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.OrderStatus;
import com.mealbroker.domain.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the OrderBroker Controller
 * Uses WebMvcTest which is lighter than a full SpringBootTest
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest
public class OrderBrokerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderBrokerService orderBrokerService;

    @MockitoBean
    private OrderServiceClient orderServiceClient;

    @MockitoBean
    private RestaurantServiceClient restaurantServiceClient;

    @MockitoBean
    private CustomerServiceClient customerServiceClient;

    @MockitoBean
    private LocationServiceClient locationServiceClient;

    @MockitoBean
    private CircuitBreakerFactory circuitBreakerFactory;

    @MockitoBean
    private CircuitBreaker circuitBreaker;

    private Location customerLocation;
    private List<Branch> branches;
    private Branch nearestBranch;
    private OrderRequestDTO orderRequest;
    private OrderResponseDTO orderResponse;
    private List<OrderHistoryDTO> orderHistoryList;

    @BeforeEach
    void setUp() {
        // Setup test data
        customerLocation = new Location(43.6532, -79.3832); // Toronto coordinates

        // Create sample branch
        nearestBranch = new Branch();
        nearestBranch.setBranchId(1L);
        nearestBranch.setBranchName("Test Branch");
        nearestBranch.setLocation(new Location(43.6547, -79.3850)); // Close to customer location
        nearestBranch.setActive(true);

        branches = new ArrayList<>();
        branches.add(nearestBranch);

        // Create order request
        orderRequest = new OrderRequestDTO();
        orderRequest.setCustomerId(1L);
        orderRequest.setRestaurantId(1L);
        orderRequest.setCustomerLocation(customerLocation);
        orderRequest.setItems(Collections.singletonList(new MenuItemDTO(1L, 2)));

        // Expected response
        orderResponse = new OrderResponseDTO();
        orderResponse.setOrderId(1L);
        orderResponse.setCustomerId(1L);
        orderResponse.setRestaurantId(1L);
        orderResponse.setBranchId(1L);
        orderResponse.setStatus(OrderStatus.NEW);
        orderResponse.setOrderTime(new Date());

        // Setup order history data
        OrderHistoryDTO history1 = new OrderHistoryDTO(1L, 1L, null, OrderStatus.NEW, new Date(), "Order created");
        OrderHistoryDTO history2 = new OrderHistoryDTO(2L, 1L, OrderStatus.NEW, OrderStatus.PROCESSING, new Date(), "Order processing started");
        orderHistoryList = Arrays.asList(history1, history2);

        // Mock Circuit Breaker behavior
        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(Supplier.class), any(Function.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    public void testPlaceOrder() throws Exception {
        // Setup mock
        when(orderBrokerService.placeOrder(any(OrderRequestDTO.class))).thenReturn(orderResponse);

        // Execute and verify
        mockMvc.perform(post("/api/broker/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    public void testUpdateOrderStatus() throws Exception {
        // Setup mock
        OrderResponseDTO updatedResponse = new OrderResponseDTO();
        updatedResponse.setOrderId(1L);
        updatedResponse.setStatus(OrderStatus.CONFIRMED);

        OrderStatusUpdateDTO statusUpdate = new OrderStatusUpdateDTO(OrderStatus.CONFIRMED);

        when(orderBrokerService.updateOrderStatus(eq(1L), eq(OrderStatus.CONFIRMED))).thenReturn(updatedResponse);

        // Execute and verify
        mockMvc.perform(put("/api/broker/orders/{orderId}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    public void testCancelOrder() throws Exception {
        // Setup mock
        OrderResponseDTO cancelledResponse = new OrderResponseDTO();
        cancelledResponse.setOrderId(1L);
        cancelledResponse.setStatus(OrderStatus.CANCELLED);

        when(orderBrokerService.cancelOrder(eq(1L))).thenReturn(cancelledResponse);

        // Execute and verify
        mockMvc.perform(post("/api/broker/orders/{orderId}/cancel", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    public void testGetOrderHistory() throws Exception {
        // Setup mock
        when(orderBrokerService.getOrderHistory(eq(1L))).thenReturn(orderHistoryList);

        // Execute and verify
        mockMvc.perform(get("/api/broker/orders/{orderId}/history", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].historyId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].newStatus").value("NEW"))
                .andExpect(jsonPath("$[0].notes").value("Order created"))
                .andExpect(jsonPath("$[1].historyId").value(2))
                .andExpect(jsonPath("$[1].previousStatus").value("NEW"))
                .andExpect(jsonPath("$[1].newStatus").value("PROCESSING"));
    }

    @Test
    public void testGetOrderHistory_NoHistory() throws Exception {
        // Setup mock
        when(orderBrokerService.getOrderHistory(eq(1L))).thenReturn(Collections.emptyList());

        // Execute and verify
        mockMvc.perform(get("/api/broker/orders/{orderId}/history", 1L))
                .andExpect(status().isNoContent());
    }


    @Test
    public void testFindNearbyBranches() throws Exception {
        // Setup mock
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(branches);
        when(locationServiceClient.findNearbyBranches(any(NearbyBranchesRequestDTO.class))).thenReturn(branches);

        // Execute and verify
        mockMvc.perform(post("/api/broker/nearby-branches")
                        .param("restaurantId", "1")
                        .param("maxDistanceKm", "10.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].branchId").value(1))
                .andExpect(jsonPath("$[0].branchName").value("Test Branch"));
    }

    @Test
    public void testFindNearestBranch() throws Exception {
        // Setup mock
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(branches);
        when(locationServiceClient.findNearestBranch(any(NearestBranchRequestDTO.class))).thenReturn(nearestBranch);

        // Execute and verify
        mockMvc.perform(post("/api/broker/nearest-branch")
                        .param("restaurantId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchId").value(1))
                .andExpect(jsonPath("$.branchName").value("Test Branch"));
    }

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/broker/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order Broker Service is up and running!"));
    }
}