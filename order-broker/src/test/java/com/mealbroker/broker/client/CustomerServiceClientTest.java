package com.mealbroker.broker.client;

import com.mealbroker.domain.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceClientTest {

    @Mock
    private CustomerServiceClient customerServiceClient;

    @BeforeEach
    void setUp() {
        // Ensure that the interface has required annotations
        assertTrue(CustomerServiceClient.class.isAnnotationPresent(FeignClient.class),
                "CustomerServiceClient should be annotated with @FeignClient");

        FeignClient feignClient = CustomerServiceClient.class.getAnnotation(FeignClient.class);
        assertEquals("customer-service", feignClient.name(),
                "FeignClient name should be 'customer-service'");
    }

    @Test
    void validateCustomerMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = CustomerServiceClient.class.getMethod("validateCustomer", Long.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class),
                "validateCustomer method should be annotated with @GetMapping");

        // Test with mock
        when(customerServiceClient.validateCustomer(1L)).thenReturn(true);
        when(customerServiceClient.validateCustomer(2L)).thenReturn(false);

        assertTrue(customerServiceClient.validateCustomer(1L));
        assertFalse(customerServiceClient.validateCustomer(2L));

        verify(customerServiceClient, times(1)).validateCustomer(1L);
        verify(customerServiceClient, times(1)).validateCustomer(2L);
    }

    @Test
    void getCustomerLocationMethodTest() throws NoSuchMethodException {
        // Verify the method exists and has correct annotations
        Method method = CustomerServiceClient.class.getMethod("getCustomerLocation", Long.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class),
                "getCustomerLocation method should be annotated with @GetMapping");

        // Create test data
        Location expectedLocation = new Location(43.6532, -79.3832);

        // Test with mock
        when(customerServiceClient.getCustomerLocation(1L)).thenReturn(expectedLocation);

        Location actualLocation = customerServiceClient.getCustomerLocation(1L);

        assertNotNull(actualLocation);
        assertEquals(expectedLocation.getLatitude(), actualLocation.getLatitude());
        assertEquals(expectedLocation.getLongitude(), actualLocation.getLongitude());

        verify(customerServiceClient, times(1)).getCustomerLocation(1L);
    }
}