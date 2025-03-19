package com.mealbroker.broker.service.impl;

import com.mealbroker.broker.client.CustomerServiceClient;
import com.mealbroker.broker.client.LocationServiceClient;
import com.mealbroker.broker.client.OrderServiceClient;
import com.mealbroker.broker.client.RestaurantServiceClient;
import com.mealbroker.broker.exception.BrokerException;
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
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderBrokerServiceImplTest {

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private RestaurantServiceClient restaurantServiceClient;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private LocationServiceClient locationServiceClient;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    @InjectMocks
    private OrderBrokerServiceImpl orderBrokerService;

    private OrderRequestDTO orderRequest;
    private Location customerLocation;
    private List<Branch> branches;
    private Branch nearestBranch;
    private OrderResponseDTO expectedResponse;
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

        branches = new ArrayList<>();
        branches.add(nearestBranch);

        // Create order request
        orderRequest = new OrderRequestDTO();
        orderRequest.setCustomerId(1L);
        orderRequest.setRestaurantId(1L);
        orderRequest.setCustomerLocation(customerLocation);
        orderRequest.setItems(Collections.singletonList(new MenuItemDTO(1L, 2)));

        // Expected response
        expectedResponse = new OrderResponseDTO();
        expectedResponse.setOrderId(1L);
        expectedResponse.setCustomerId(1L);
        expectedResponse.setRestaurantId(1L);
        expectedResponse.setBranchId(1L);
        expectedResponse.setStatus(OrderStatus.NEW);
        expectedResponse.setOrderTime(new Date());

        // Setup order history data
        OrderHistoryDTO history1 = new OrderHistoryDTO(1L, 1L, null, OrderStatus.NEW, new Date(), "Order created");
        OrderHistoryDTO history2 = new OrderHistoryDTO(2L, 1L, OrderStatus.NEW, OrderStatus.PROCESSING, new Date(), "Order processing started");
        orderHistoryList = Arrays.asList(history1, history2);

        // Mock circuit breaker behavior
        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    void placeOrder_Success() {
        // Setup mocks
        when(customerServiceClient.validateCustomer(anyLong())).thenReturn(true);
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(branches);
        when(locationServiceClient.findNearestBranch(any(NearestBranchRequestDTO.class))).thenReturn(nearestBranch);
        when(restaurantServiceClient.checkItemsAvailability(anyLong(), anyList())).thenReturn(true);
        when(orderServiceClient.createOrder(any(OrderCreateRequestDTO.class))).thenReturn(expectedResponse);

        // Execute test
        OrderResponseDTO actualResponse = orderBrokerService.placeOrder(orderRequest);

        // Verify
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getOrderId(), actualResponse.getOrderId());
        assertEquals(expectedResponse.getCustomerId(), actualResponse.getCustomerId());
        assertEquals(expectedResponse.getRestaurantId(), actualResponse.getRestaurantId());
        assertEquals(expectedResponse.getBranchId(), actualResponse.getBranchId());

        // Verify method calls
        verify(customerServiceClient).validateCustomer(1L);
        verify(restaurantServiceClient).getBranchesByRestaurant(1L);
        verify(locationServiceClient).findNearestBranch(any(NearestBranchRequestDTO.class));
        verify(restaurantServiceClient).checkItemsAvailability(eq(1L), anyList());
        verify(orderServiceClient).createOrder(any(OrderCreateRequestDTO.class));
    }

    @Test
    void placeOrder_InvalidCustomer() {
        // Setup mocks
        when(customerServiceClient.validateCustomer(anyLong())).thenReturn(false);

        // Execute and verify
        Exception exception = assertThrows(BrokerException.class, () -> {
            orderBrokerService.placeOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("Invalid customer ID"));
        verify(customerServiceClient).validateCustomer(1L);
        verifyNoInteractions(orderServiceClient);
    }

    @Test
    void placeOrder_NoBranches() {
        // Setup mocks
        when(customerServiceClient.validateCustomer(anyLong())).thenReturn(true);
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(Collections.emptyList());

        // Execute and verify
        Exception exception = assertThrows(BrokerException.class, () -> {
            orderBrokerService.placeOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("No branches found"));
        verify(customerServiceClient).validateCustomer(1L);
        verify(restaurantServiceClient).getBranchesByRestaurant(1L);
        verifyNoInteractions(orderServiceClient);
    }

    @Test
    void placeOrder_NoNearestBranch() {
        // Setup mocks
        when(customerServiceClient.validateCustomer(anyLong())).thenReturn(true);
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(branches);
        when(locationServiceClient.findNearestBranch(any(NearestBranchRequestDTO.class))).thenReturn(null);

        // Execute and verify
        Exception exception = assertThrows(BrokerException.class, () -> {
            orderBrokerService.placeOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("Could not determine the nearest branch"));
        verify(customerServiceClient).validateCustomer(1L);
        verify(restaurantServiceClient).getBranchesByRestaurant(1L);
        verify(locationServiceClient).findNearestBranch(any(NearestBranchRequestDTO.class));
        verifyNoInteractions(orderServiceClient);
    }

    @Test
    void placeOrder_ItemsNotAvailable() {
        // Setup mocks
        when(customerServiceClient.validateCustomer(anyLong())).thenReturn(true);
        when(restaurantServiceClient.getBranchesByRestaurant(anyLong())).thenReturn(branches);
        when(locationServiceClient.findNearestBranch(any(NearestBranchRequestDTO.class))).thenReturn(nearestBranch);
        when(restaurantServiceClient.checkItemsAvailability(anyLong(), anyList())).thenReturn(false);

        // Execute and verify
        Exception exception = assertThrows(BrokerException.class, () -> {
            orderBrokerService.placeOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("Some items are not available"));
        verify(customerServiceClient).validateCustomer(1L);
        verify(restaurantServiceClient).getBranchesByRestaurant(1L);
        verify(locationServiceClient).findNearestBranch(any(NearestBranchRequestDTO.class));
        verify(restaurantServiceClient).checkItemsAvailability(eq(1L), anyList());
        verifyNoInteractions(orderServiceClient);
    }

    @Test
    void updateOrderStatus_Success() {
        // Setup mocks
        OrderResponseDTO expectedUpdateResponse = new OrderResponseDTO();
        expectedUpdateResponse.setOrderId(1L);
        expectedUpdateResponse.setStatus(OrderStatus.CONFIRMED);

        when(orderServiceClient.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(expectedUpdateResponse);

        // Execute test
        OrderResponseDTO actualResponse = orderBrokerService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Verify
        assertNotNull(actualResponse);
        assertEquals(OrderStatus.CONFIRMED, actualResponse.getStatus());

        // Verify method calls
        verify(orderServiceClient).updateOrderStatus(1L, OrderStatus.CONFIRMED);
    }

    @Test
    void cancelOrder_Success() {
        // Setup mocks
        OrderResponseDTO expectedCancelResponse = new OrderResponseDTO();
        expectedCancelResponse.setOrderId(1L);
        expectedCancelResponse.setStatus(OrderStatus.CANCELLED);

        when(orderServiceClient.cancelOrder(anyLong())).thenReturn(expectedCancelResponse);

        // Execute test
        OrderResponseDTO actualResponse = orderBrokerService.cancelOrder(1L);

        // Verify
        assertNotNull(actualResponse);
        assertEquals(OrderStatus.CANCELLED, actualResponse.getStatus());

        // Verify method calls
        verify(orderServiceClient).cancelOrder(1L);
    }

    @Test
    void getOrderHistory_Success() {
        // Setup mocks
        when(orderServiceClient.getOrderHistory(anyLong())).thenReturn(orderHistoryList);

        // Execute test
        List<OrderHistoryDTO> result = orderBrokerService.getOrderHistory(1L);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(OrderStatus.NEW, result.get(0).getNewStatus());
        assertEquals("Order created", result.get(0).getNotes());
        assertEquals(OrderStatus.PROCESSING, result.get(1).getNewStatus());
        assertEquals("Order processing started", result.get(1).getNotes());

        // Verify method calls
        verify(orderServiceClient).getOrderHistory(1L);
    }

    @Test
    void getOrderHistory_EmptyResult() {
        // Setup mocks
        when(orderServiceClient.getOrderHistory(anyLong())).thenReturn(Collections.emptyList());

        // Execute test
        List<OrderHistoryDTO> result = orderBrokerService.getOrderHistory(1L);

        // Verify
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify method calls
        verify(orderServiceClient).getOrderHistory(1L);
    }
}