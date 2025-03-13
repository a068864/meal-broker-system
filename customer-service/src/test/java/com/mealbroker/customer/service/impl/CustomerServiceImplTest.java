package com.mealbroker.customer.service.impl;

import com.mealbroker.customer.exception.CustomerNotFoundException;
import com.mealbroker.customer.exception.EmailAlreadyExistsException;
import com.mealbroker.customer.repository.CustomerRepository;
import com.mealbroker.domain.Customer;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.CustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;
    private CustomerDTO testCustomerDTO;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        // Create test location
        testLocation = new Location(40.7128, -74.0060);

        // Create test customer entity
        testCustomer = new Customer("John Doe", "john@example.com", "+12345678901");
        testCustomer.setCustomerId(1L);
        testCustomer.setLocation(testLocation);

        // Create test customer DTO
        testCustomerDTO = new CustomerDTO(1L, "John Doe", "john@example.com", "+12345678901", testLocation);
    }

    @Test
    void testCreateCustomer_Success() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        CustomerDTO result = customerService.createCustomer(testCustomerDTO);

        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getCustomerId(), result.getCustomerId());
        assertEquals(testCustomer.getName(), result.getName());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        assertEquals(testCustomer.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(testLocation.getLatitude(), result.getLocation().getLatitude());
        assertEquals(testLocation.getLongitude(), result.getLocation().getLongitude());

        verify(customerRepository, times(1)).existsByEmail(anyString());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_EmailAlreadyExists() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            customerService.createCustomer(testCustomerDTO);
        });

        verify(customerRepository, times(1)).existsByEmail(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testGetCustomer_Success() {
        // Given
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));

        // When
        CustomerDTO result = customerService.getCustomer(1L);

        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getCustomerId(), result.getCustomerId());
        assertEquals(testCustomer.getName(), result.getName());
        assertEquals(testCustomer.getEmail(), result.getEmail());

        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCustomer_NotFound() {
        // Given
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomer(99L);
        });

        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetAllCustomers_Success() {
        // Given
        Customer customer2 = new Customer("Jane Doe", "jane@example.com", "+19876543210");
        customer2.setCustomerId(2L);

        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        // When
        List<CustomerDTO> results = customerService.getAllCustomers();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("John Doe", results.get(0).getName());
        assertEquals("Jane Doe", results.get(1).getName());

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCustomer_Success() {
        // Given
        CustomerDTO updateDTO = new CustomerDTO(1L, "Updated Name", "john@example.com", "+12345678901", testLocation);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        CustomerDTO result = customerService.updateCustomer(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(updateDTO.getName(), result.getName());

        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_NotFound() {
        // Given
        CustomerDTO updateDTO = new CustomerDTO(99L, "Updated Name", "john@example.com", "+12345678901", testLocation);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(99L, updateDTO);
        });

        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_EmailAlreadyExists() {
        // Given
        CustomerDTO updateDTO = new CustomerDTO(1L, "John Doe", "new@example.com", "+12345678901", testLocation);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            customerService.updateCustomer(1L, updateDTO);
        });

        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, times(1)).existsByEmail(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomer_Success() {
        // Given
        when(customerRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(customerRepository).deleteById(anyLong());

        // When
        customerService.deleteCustomer(1L);

        // Then
        verify(customerRepository, times(1)).existsById(anyLong());
        verify(customerRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testDeleteCustomer_NotFound() {
        // Given
        when(customerRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.deleteCustomer(99L);
        });

        verify(customerRepository, times(1)).existsById(anyLong());
        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetCustomerByEmail_Success() {
        // Given
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(testCustomer));

        // When
        CustomerDTO result = customerService.getCustomerByEmail("john@example.com");

        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getEmail(), result.getEmail());

        verify(customerRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testGetCustomerByEmail_NotFound() {
        // Given
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerByEmail("notfound@example.com");
        });

        verify(customerRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testValidateCustomer_Success() {
        // Given
        when(customerRepository.existsById(anyLong())).thenReturn(true);

        // When
        boolean result = customerService.validateCustomer(1L);

        // Then
        assertTrue(result);

        verify(customerRepository, times(1)).existsById(anyLong());
    }

    @Test
    void testValidateCustomer_NotFound() {
        // Given
        when(customerRepository.existsById(anyLong())).thenReturn(false);

        // When
        boolean result = customerService.validateCustomer(99L);

        // Then
        assertFalse(result);

        verify(customerRepository, times(1)).existsById(anyLong());
    }

    @Test
    void testGetCustomerLocation_Success() {
        // Given
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));

        // When
        Location result = customerService.getCustomerLocation(1L);

        // Then
        assertNotNull(result);
        assertEquals(testLocation.getLatitude(), result.getLatitude());
        assertEquals(testLocation.getLongitude(), result.getLongitude());

        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCustomerLocation_NotFound() {
        // Given
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerLocation(99L);
        });

        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCustomerLocation_LocationNotSet() {
        // Given
        Customer customerWithoutLocation = new Customer("John Doe", "john@example.com", "+12345678901");
        customerWithoutLocation.setCustomerId(1L);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customerWithoutLocation));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            customerService.getCustomerLocation(1L);
        });

        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateCustomerLocation_Success() {
        // Given
        Location newLocation = new Location(38.8951, -77.0364);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        CustomerDTO result = customerService.updateCustomerLocation(1L, newLocation);

        // Then
        assertNotNull(result);

        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomerLocation_NotFound() {
        // Given
        Location newLocation = new Location(38.8951, -77.0364);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomerLocation(99L, newLocation);
        });

        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testSearchCustomers_Success() {
        // Given
        String searchTerm = "john";
        Customer customer1 = testCustomer;
        Customer customer2 = new Customer("Johnny Smith", "johnny@example.com", "+12345678901");
        customer2.setCustomerId(2L);

        List<Customer> searchResults = Arrays.asList(customer1, customer2);

        when(customerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                anyString(), anyString(), anyString())).thenReturn(searchResults);

        // When
        List<CustomerDTO> results = customerService.searchCustomers(searchTerm);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("John Doe", results.get(0).getName());
        assertEquals("Johnny Smith", results.get(1).getName());

        verify(customerRepository, times(1))
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                        anyString(), anyString(), anyString());
    }

    @Test
    void testSearchCustomers_EmptyQuery() {
        // Given
        String searchTerm = "";

        // When
        List<CustomerDTO> results = customerService.searchCustomers(searchTerm);

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(customerRepository, never())
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                        anyString(), anyString(), anyString());
    }

    @Test
    void testSearchCustomers_NullQuery() {
        // Given
        String searchTerm = null;

        // When
        List<CustomerDTO> results = customerService.searchCustomers(searchTerm);

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(customerRepository, never())
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                        anyString(), anyString(), anyString());
    }
}