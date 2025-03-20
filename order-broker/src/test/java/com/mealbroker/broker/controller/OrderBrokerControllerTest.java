package com.mealbroker.broker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealbroker.broker.client.LocationServiceClient;
import com.mealbroker.broker.client.RestaurantServiceClient;
import com.mealbroker.broker.service.OrderBrokerService;
import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.OrderStatus;
import com.mealbroker.domain.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderBrokerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderBrokerService orderBrokerService;

    @Mock
    private RestaurantServiceClient restaurantServiceClient;

    @Mock
    private LocationServiceClient locationServiceClient;

    @InjectMocks
    private OrderBrokerController orderBrokerController;

    private ObjectMapper objectMapper;
    private OrderRequestDTO orderRequest;
    private OrderResponseDTO orderResponse;
    private Location customerLocation;
    private Branch nearestBranch;
    private List<Branch> branches;
    private List<OrderHistoryDTO> orderHistoryList;
    ;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(orderBrokerController).build();

        // Setup test data
        customerLocation = new Location(43.6532, -79.3832); // Toronto coordinates

        // Setup order request
        orderRequest = new OrderRequestDTO();
        orderRequest.setCustomerId(1L);
        orderRequest.setRestaurantId(1L);
        orderRequest.setCustomerLocation(customerLocation);
        orderRequest.setItems(Collections.singletonList(new MenuItemDTO(1L, 2)));

        // Setup order response
        orderResponse = new OrderResponseDTO();
        orderResponse.setOrderId(1L);
        orderResponse.setCustomerId(1L);
        orderResponse.setRestaurantId(1L);
        orderResponse.setBranchId(1L);
        orderResponse.setStatus(OrderStatus.NEW);
        orderResponse.setOrderTime(new Date());

        // Setup branch
        nearestBranch = new Branch();
        nearestBranch.setBranchId(1L);
        nearestBranch.setBranchName("Test Branch");
        nearestBranch.setLocation(new Location(43.6547, -79.3850)); // Close to customer location

        branches = new ArrayList<>();
        branches.add(nearestBranch);

        // Setup order history data
        OrderHistoryDTO history1 = new OrderHistoryDTO(1L, 1L, null, OrderStatus.NEW, new Date(), "Order created");
        OrderHistoryDTO history2 = new OrderHistoryDTO(2L, 1L, OrderStatus.NEW, OrderStatus.PROCESSING, new Date(), "Order processing started");
        orderHistoryList = Arrays.asList(history1, history2);
    }

    @Test
    void placeOrder_Success() throws Exception {
        // Setup mock
        when(orderBrokerService.placeOrder(any(OrderRequestDTO.class))).thenReturn(orderResponse);

        // Execute and verify
        mockMvc.perform(post("/api/broker/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(1)))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.status", is("NEW")));

        verify(orderBrokerService).placeOrder(any(OrderRequestDTO.class));
    }

    @Test
    void updateOrderStatus_Success() throws Exception {
        // Setup mock
        OrderResponseDTO updatedResponse = new OrderResponseDTO();
        updatedResponse.setOrderId(1L);
        updatedResponse.setStatus(OrderStatus.CONFIRMED);

        OrderStatusUpdateDTO statusUpdate = new OrderStatusUpdateDTO(OrderStatus.CONFIRMED);

        when(orderBrokerService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(updatedResponse);

        // Execute and verify
        mockMvc.perform(put("/api/broker/orders/{orderId}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(1)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));

        verify(orderBrokerService).updateOrderStatus(eq(1L), eq(OrderStatus.CONFIRMED));
    }

    @Test
    void cancelOrder_Success() throws Exception {
        // Setup mock
        OrderResponseDTO cancelledResponse = new OrderResponseDTO();
        cancelledResponse.setOrderId(1L);
        cancelledResponse.setStatus(OrderStatus.CANCELLED);

        when(orderBrokerService.cancelOrder(anyLong())).thenReturn(cancelledResponse);

        // Execute and verify
        mockMvc.perform(post("/api/broker/orders/{orderId}/cancel", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(1)))
                .andExpect(jsonPath("$.status", is("CANCELLED")));

        verify(orderBrokerService).cancelOrder(1L);
    }

    @Test
    void getOrderHistory_Success() throws Exception {
        // Setup mock
        when(orderBrokerService.getOrderHistory(anyLong())).thenReturn(orderHistoryList);

        // Execute and verify
        mockMvc.perform(get("/api/broker/orders/{orderId}/history", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].historyId", is(1)))
                .andExpect(jsonPath("$[0].orderId", is(1)))
                .andExpect(jsonPath("$[0].newStatus", is("NEW")))
                .andExpect(jsonPath("$[0].notes", is("Order created")))
                .andExpect(jsonPath("$[1].historyId", is(2)))
                .andExpect(jsonPath("$[1].previousStatus", is("NEW")))
                .andExpect(jsonPath("$[1].newStatus", is("PROCESSING")));

        verify(orderBrokerService).getOrderHistory(1L);
    }

    @Test
    void getOrderHistory_EmptyResult() throws Exception {
        // Setup mock
        when(orderBrokerService.getOrderHistory(anyLong())).thenReturn(Collections.emptyList());

        // Execute and verify
        mockMvc.perform(get("/api/broker/orders/{orderId}/history", 1L))
                .andExpect(status().isNoContent());

        verify(orderBrokerService).getOrderHistory(1L);
    }

    @Test
    void findNearbyBranches_Success() throws Exception {
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
                .andExpect(jsonPath("$[0].branchId", is(1)))
                .andExpect(jsonPath("$[0].branchName", is("Test Branch")));

        verify(restaurantServiceClient).getBranchesByRestaurant(1L);
        verify(locationServiceClient).findNearbyBranches(any(NearbyBranchesRequestDTO.class));
    }

    @Test
    void findNearbyBranches_NoBranches() throws Exception {
        // Setup mock
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(Collections.emptyList());

        // Execute and verify
        mockMvc.perform(post("/api/broker/nearby-branches")
                        .param("restaurantId", "1")
                        .param("maxDistanceKm", "10.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerLocation)))
                .andExpect(status().isNoContent());

        verify(restaurantServiceClient).getBranchesByRestaurant(1L);
    }

    @Test
    void findNearestBranch_Success() throws Exception {
        // Setup mock
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(branches);
        when(locationServiceClient.findNearestBranch(any(NearestBranchRequestDTO.class))).thenReturn(nearestBranch);

        // Execute and verify
        mockMvc.perform(post("/api/broker/nearest-branch")
                        .param("restaurantId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchId", is(1)))
                .andExpect(jsonPath("$.branchName", is("Test Branch")));

        verify(restaurantServiceClient).getBranchesByRestaurant(1L);
        verify(locationServiceClient).findNearestBranch(any(NearestBranchRequestDTO.class));
    }

    @Test
    void findNearestBranch_NoBranches() throws Exception {
        // Setup mock
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(Collections.emptyList());

        // Execute and verify
        mockMvc.perform(post("/api/broker/nearest-branch")
                        .param("restaurantId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerLocation)))
                .andExpect(status().isNoContent());

        verify(restaurantServiceClient).getBranchesByRestaurant(1L);
    }

    @Test
    void findNearestBranch_NoNearestBranch() throws Exception {
        // Setup mock
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(branches);
        when(locationServiceClient.findNearestBranch(any(NearestBranchRequestDTO.class))).thenReturn(null);

        // Execute and verify
        mockMvc.perform(post("/api/broker/nearest-branch")
                        .param("restaurantId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerLocation)))
                .andExpect(status().isNoContent());

        verify(restaurantServiceClient).getBranchesByRestaurant(1L);
        verify(locationServiceClient).findNearestBranch(any(NearestBranchRequestDTO.class));
    }

    @Test
    void health_Success() throws Exception {
        mockMvc.perform(get("/api/broker/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order Broker Service is up and running!"));
    }
}