package com.mealbroker.order.service.impl;

import com.mealbroker.domain.*;
import com.mealbroker.domain.dto.OrderDTO;
import com.mealbroker.domain.dto.OrderHistoryDTO;
import com.mealbroker.domain.dto.OrderItemDTO;
import com.mealbroker.order.exception.OrderNotFoundException;
import com.mealbroker.order.repository.OrderHistoryRepository;
import com.mealbroker.order.repository.OrderItemRepository;
import com.mealbroker.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderHistoryRepository orderHistoryRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer customer;
    private Restaurant restaurant;
    private Branch branch;
    private Order order;
    private OrderItem orderItem;
    private Location customerLocation;
    private OrderHistory orderHistory1;
    private OrderHistory orderHistory2;
    private List<OrderHistory> orderHistoryList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up test data
        customerLocation = new Location(43.6532, -79.3832); // Toronto coordinates

        customer = new Customer("John Doe", "john@example.com", "+12345678901");
        customer.setCustomerId(1L);

        restaurant = new Restaurant("McDonald's", "Fast Food");
        restaurant.setRestaurantId(2L);

        branch = new Branch("Downtown", new Location(43.6532, -79.3832));
        branch.setBranchId(3L);
        branch.setRestaurant(restaurant);

        order = new Order(customer, restaurant);
        order.setOrderId(4L);
        order.setBranch(branch);
        order.setOrderTime(new Date());
        order.setCustomerLocation(customerLocation);

        orderItem = new OrderItem(101L, "Big Mac", 1, 5.99);
        orderItem.setOrderItemId(5L);
        order.addItem(orderItem);

        // Set up order history
        orderHistory1 = new OrderHistory(order, null, OrderStatus.NEW, "Order created");
        orderHistory1.setHistoryId(1L);
        orderHistory1.setTimestamp(new Date());

        orderHistory2 = new OrderHistory(order, OrderStatus.NEW, OrderStatus.PROCESSING, "Order processing started");
        orderHistory2.setHistoryId(2L);
        orderHistory2.setTimestamp(new Date());

        orderHistoryList = Arrays.asList(orderHistory1, orderHistory2);
    }

    @Test
    void createOrder_shouldCreateNewOrder() {
        // Arrange
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerId(customer.getCustomerId());
        orderDTO.setRestaurantId(restaurant.getRestaurantId());
        orderDTO.setBranchId(branch.getBranchId());
        orderDTO.setCustomerLocation(customerLocation);

        OrderItemDTO itemDTO = new OrderItemDTO(101L, 1, 5.99);
        itemDTO.setMenuItemName("Big Mac");
        List<OrderItemDTO> items = Collections.singletonList(itemDTO);
        orderDTO.setItems(items);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setOrderId(4L);
            return savedOrder;
        });

        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem savedItem = invocation.getArgument(0);
            savedItem.setOrderItemId(5L);
            return savedItem;
        });

        when(orderHistoryRepository.save(any(OrderHistory.class))).thenReturn(orderHistory1);

        // Act
        OrderDTO result = orderService.createOrder(orderDTO);

        // Assert
        assertNotNull(result);
        assertEquals(4L, result.getOrderId());
        assertEquals(customer.getCustomerId(), result.getCustomerId());
        assertEquals(restaurant.getRestaurantId(), result.getRestaurantId());
        assertEquals(branch.getBranchId(), result.getBranchId());
        assertEquals(OrderStatus.NEW, result.getStatus());
        assertNotNull(result.getOrderTime());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(5.99, result.getItems().get(0).getPrice());
        assertEquals("Big Mac", result.getItems().get(0).getMenuItemName());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(orderHistoryRepository, times(1)).save(any(OrderHistory.class));
    }

    @Test
    void getOrder_shouldReturnOrder() {
        // Arrange
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        // Act
        OrderDTO result = orderService.getOrder(order.getOrderId());

        // Assert
        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        assertEquals(customer.getCustomerId(), result.getCustomerId());
        assertEquals(restaurant.getRestaurantId(), result.getRestaurantId());
        assertEquals(branch.getBranchId(), result.getBranchId());
        assertEquals(order.getStatus(), result.getStatus());
        assertEquals(order.getOrderTime(), result.getOrderTime());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());

        verify(orderRepository, times(1)).findById(order.getOrderId());
    }

    @Test
    void getOrder_withNonExistentId_shouldThrowException() {
        // Arrange
        Long nonExistentId = 999L;
        when(orderRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(nonExistentId));
        verify(orderRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void getOrdersByCustomer_shouldReturnOrders() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.findByCustomerCustomerId(customer.getCustomerId())).thenReturn(orders);

        // Act
        List<OrderDTO> results = orderService.getOrdersByCustomer(customer.getCustomerId());

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(order.getOrderId(), results.get(0).getOrderId());

        verify(orderRepository, times(1)).findByCustomerCustomerId(customer.getCustomerId());
    }

    @Test
    void getOrdersByCustomerPaginated_shouldReturnPagedOrders() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);
        Page<Order> orderPage = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findByCustomerCustomerId(customer.getCustomerId(), pageable)).thenReturn(orderPage);

        // Act
        Page<OrderDTO> result = orderService.getOrdersByCustomer(customer.getCustomerId(), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(order.getOrderId(), result.getContent().get(0).getOrderId());

        verify(orderRepository, times(1)).findByCustomerCustomerId(customer.getCustomerId(), pageable);
    }

    @Test
    void getOrdersByRestaurant_shouldReturnOrders() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.findByRestaurantRestaurantId(restaurant.getRestaurantId())).thenReturn(orders);

        // Act
        List<OrderDTO> results = orderService.getOrdersByRestaurant(restaurant.getRestaurantId());

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(order.getOrderId(), results.get(0).getOrderId());

        verify(orderRepository, times(1)).findByRestaurantRestaurantId(restaurant.getRestaurantId());
    }

    @Test
    void getOrdersByBranch_shouldReturnOrders() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.findByBranchBranchId(branch.getBranchId())).thenReturn(orders);

        // Act
        List<OrderDTO> results = orderService.getOrdersByBranch(branch.getBranchId());

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(order.getOrderId(), results.get(0).getOrderId());

        verify(orderRepository, times(1)).findByBranchBranchId(branch.getBranchId());
    }

    @Test
    void getOrdersByStatus_shouldReturnOrders() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.findByStatus(OrderStatus.NEW)).thenReturn(orders);

        // Act
        List<OrderDTO> results = orderService.getOrdersByStatus(OrderStatus.NEW);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(order.getOrderId(), results.get(0).getOrderId());
        assertEquals(OrderStatus.NEW, results.get(0).getStatus());

        verify(orderRepository, times(1)).findByStatus(OrderStatus.NEW);
    }

    @Test
    void getOrdersByBranchAndStatus_shouldReturnOrders() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.findByBranchBranchIdAndStatus(branch.getBranchId(), OrderStatus.NEW)).thenReturn(orders);

        // Act
        List<OrderDTO> results = orderService.getOrdersByBranchAndStatus(branch.getBranchId(), OrderStatus.NEW);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(order.getOrderId(), results.get(0).getOrderId());
        assertEquals(branch.getBranchId(), results.get(0).getBranchId());
        assertEquals(OrderStatus.NEW, results.get(0).getStatus());

        verify(orderRepository, times(1)).findByBranchBranchIdAndStatus(branch.getBranchId(), OrderStatus.NEW);
    }

    @Test
    void getOrdersByCustomerAndStatus_shouldReturnOrders() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.findByCustomerCustomerIdAndStatus(customer.getCustomerId(), OrderStatus.NEW)).thenReturn(orders);

        // Act
        List<OrderDTO> results = orderService.getOrdersByCustomerAndStatus(customer.getCustomerId(), OrderStatus.NEW);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(order.getOrderId(), results.get(0).getOrderId());
        assertEquals(customer.getCustomerId(), results.get(0).getCustomerId());
        assertEquals(OrderStatus.NEW, results.get(0).getStatus());

        verify(orderRepository, times(1)).findByCustomerCustomerIdAndStatus(customer.getCustomerId(), OrderStatus.NEW);
    }

    @Test
    void getOrdersByDateRange_shouldReturnOrders() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);
        Date startDate = new Date(System.currentTimeMillis() - 86400000); // 1 day ago
        Date endDate = new Date(System.currentTimeMillis() + 86400000);   // 1 day in future

        when(orderRepository.findByOrderTimeBetween(startDate, endDate)).thenReturn(orders);

        // Act
        List<OrderDTO> results = orderService.getOrdersByDateRange(startDate, endDate);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(order.getOrderId(), results.get(0).getOrderId());

        verify(orderRepository, times(1)).findByOrderTimeBetween(startDate, endDate);
    }

    @Test
    void updateOrderStatus_shouldUpdateStatus() {
        // Arrange
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderHistoryRepository.save(any(OrderHistory.class))).thenReturn(orderHistory2);

        // Act
        OrderDTO result = orderService.updateOrderStatus(order.getOrderId(), OrderStatus.PROCESSING);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.PROCESSING, result.getStatus());

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderHistoryRepository, times(1)).save(any(OrderHistory.class));
    }

    @Test
    void cancelOrder_shouldCancelOrder() {
        // Arrange
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderHistoryRepository.save(any(OrderHistory.class))).thenReturn(new OrderHistory());

        // Act
        OrderDTO result = orderService.cancelOrder(order.getOrderId());

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderHistoryRepository, times(1)).save(any(OrderHistory.class));
    }

    @Test
    void getOrderHistory_shouldReturnHistoryList() {
        // Arrange
        when(orderRepository.existsById(order.getOrderId())).thenReturn(true);
        when(orderHistoryRepository.findByOrderOrderId(order.getOrderId())).thenReturn(orderHistoryList);

        // Act
        List<OrderHistoryDTO> results = orderService.getOrderHistory(order.getOrderId());

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(orderHistory1.getHistoryId(), results.get(0).getHistoryId());
        assertEquals(orderHistory1.getOrder().getOrderId(), results.get(0).getOrderId());
        assertEquals(orderHistory1.getNewStatus(), results.get(0).getNewStatus());
        assertEquals(orderHistory1.getNotes(), results.get(0).getNotes());

        assertEquals(orderHistory2.getHistoryId(), results.get(1).getHistoryId());
        assertEquals(orderHistory2.getPreviousStatus(), results.get(1).getPreviousStatus());
        assertEquals(orderHistory2.getNewStatus(), results.get(1).getNewStatus());
        assertEquals(orderHistory2.getNotes(), results.get(1).getNotes());

        verify(orderRepository, times(1)).existsById(order.getOrderId());
        verify(orderHistoryRepository, times(1)).findByOrderOrderId(order.getOrderId());
    }

    @Test
    void getOrderHistory_orderNotFound_shouldThrowException() {
        // Arrange
        Long nonExistentId = 999L;
        when(orderRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderHistory(nonExistentId));
        verify(orderRepository, times(1)).existsById(nonExistentId);
        verify(orderHistoryRepository, never()).findByOrderOrderId(anyLong());
    }
}