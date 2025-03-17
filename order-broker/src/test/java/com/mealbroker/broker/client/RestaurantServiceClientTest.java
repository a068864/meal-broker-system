package com.mealbroker.broker.client;

import com.mealbroker.domain.Branch;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.MenuItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceClientTest {

    @Mock
    private RestaurantServiceClient restaurantServiceClient;

    private List<Branch> testBranches;
    private List<MenuItemDTO> testMenuItems;

    @BeforeEach
    void setUp() {
        // Ensure that the interface has required annotations
        assertTrue(RestaurantServiceClient.class.isAnnotationPresent(FeignClient.class),
                "RestaurantServiceClient should be annotated with @FeignClient");

        FeignClient feignClient = RestaurantServiceClient.class.getAnnotation(FeignClient.class);
        assertEquals("restaurant-service", feignClient.name(),
                "FeignClient name should be 'restaurant-service'");

        // Create test data
        testBranches = new ArrayList<>();

        Branch branch1 = new Branch();
        branch1.setBranchId(1L);
        branch1.setBranchName("Test Branch 1");
        branch1.setLocation(new Location(43.6532, -79.3832));
        branch1.setActive(true);

        Branch branch2 = new Branch();
        branch2.setBranchId(2L);
        branch2.setBranchName("Test Branch 2");
        branch2.setLocation(new Location(43.7532, -79.4832));
        branch2.setActive(false);

        testBranches.add(branch1);
        testBranches.add(branch2);

        // Create test menu items
        testMenuItems = new ArrayList<>();

        MenuItemDTO item1 = new MenuItemDTO(1L, 2);
        MenuItemDTO item2 = new MenuItemDTO(2L, 1);

        testMenuItems.add(item1);
        testMenuItems.add(item2);
    }

    @Test
    void getBranchesByRestaurantMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = RestaurantServiceClient.class.getMethod("getBranchesByRestaurant", Long.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class),
                "getBranchesByRestaurant method should be annotated with @GetMapping");

        // Test with mock
        when(restaurantServiceClient.getBranchesByRestaurant(1L)).thenReturn(testBranches);

        List<Branch> branches = restaurantServiceClient.getBranchesByRestaurant(1L);

        assertNotNull(branches);
        assertEquals(2, branches.size());
        assertEquals(1L, branches.get(0).getBranchId());
        assertEquals("Test Branch 1", branches.get(0).getBranchName());
        assertTrue(branches.get(0).isActive());
        assertEquals(2L, branches.get(1).getBranchId());
        assertEquals("Test Branch 2", branches.get(1).getBranchName());
        assertFalse(branches.get(1).isActive());

        verify(restaurantServiceClient, times(1)).getBranchesByRestaurant(1L);
    }

    @Test
    void checkItemsAvailabilityMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = RestaurantServiceClient.class.getMethod("checkItemsAvailability", Long.class, List.class);
        assertTrue(method.isAnnotationPresent(PostMapping.class),
                "checkItemsAvailability method should be annotated with @PostMapping");

        // Test with mock - items available
        when(restaurantServiceClient.checkItemsAvailability(1L, testMenuItems)).thenReturn(true);
        // Test with mock - items not available
        when(restaurantServiceClient.checkItemsAvailability(2L, testMenuItems)).thenReturn(false);

        boolean available1 = restaurantServiceClient.checkItemsAvailability(1L, testMenuItems);
        boolean available2 = restaurantServiceClient.checkItemsAvailability(2L, testMenuItems);

        assertTrue(available1);
        assertFalse(available2);

        verify(restaurantServiceClient, times(1)).checkItemsAvailability(1L, testMenuItems);
        verify(restaurantServiceClient, times(1)).checkItemsAvailability(2L, testMenuItems);
    }
}