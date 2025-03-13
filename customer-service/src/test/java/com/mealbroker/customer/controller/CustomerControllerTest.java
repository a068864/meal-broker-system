package com.mealbroker.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealbroker.customer.exception.CustomerNotFoundException;
import com.mealbroker.customer.exception.EmailAlreadyExistsException;
import com.mealbroker.customer.service.CustomerService;
import com.mealbroker.domain.Location;
import com.mealbroker.domain.dto.CustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerDTO testCustomerDTO;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        // Create test location
        testLocation = new Location(40.7128, -74.0060);

        // Create test customer DTO
        testCustomerDTO = new CustomerDTO(1L, "John Doe", "john@example.com", "+12345678901", testLocation);
    }

    @Test
    void testCreateCustomer_Success() throws Exception {
        // Given
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(testCustomerDTO);

        // When/Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("+12345678901")));

        verify(customerService, times(1)).createCustomer(any(CustomerDTO.class));
    }

    @Test
    void testCreateCustomer_ValidationError() throws Exception {
        // Invalid DTO - missing required name
        CustomerDTO invalidDTO = new CustomerDTO();
        invalidDTO.setEmail("john@example.com");
        invalidDTO.setPhoneNumber("+12345678901");

        // When/Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any(CustomerDTO.class));
    }

    @Test
    void testCreateCustomer_EmailAlreadyExists() throws Exception {
        // Given
        when(customerService.createCustomer(any(CustomerDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already registered"));

        // When/Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isConflict());

        verify(customerService, times(1)).createCustomer(any(CustomerDTO.class));
    }

    @Test
    void testGetCustomer_Success() throws Exception {
        // Given
        when(customerService.getCustomer(anyLong())).thenReturn(testCustomerDTO);

        // When/Then
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(customerService, times(1)).getCustomer(anyLong());
    }

    @Test
    void testGetCustomer_NotFound() throws Exception {
        // Given
        when(customerService.getCustomer(anyLong()))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        // When/Then
        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomer(anyLong());
    }

    @Test
    void testGetAllCustomers_Success() throws Exception {
        // Given
        CustomerDTO customer2 = new CustomerDTO(2L, "Jane Doe", "jane@example.com", "+19876543210", testLocation);
        List<CustomerDTO> customers = Arrays.asList(testCustomerDTO, customer2);

        when(customerService.getAllCustomers()).thenReturn(customers);

        // When/Then
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].customerId", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].customerId", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void testUpdateCustomer_Success() throws Exception {
        // Given
        CustomerDTO updateDTO = new CustomerDTO(1L, "Updated Name", "john@example.com", "+12345678901", testLocation);

        when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class))).thenReturn(updateDTO);

        // When/Then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")));

        verify(customerService, times(1)).updateCustomer(eq(1L), any(CustomerDTO.class));
    }

    @Test
    void testUpdateCustomer_NotFound() throws Exception {
        // Given
        CustomerDTO updateDTO = new CustomerDTO(99L, "Updated Name", "john@example.com", "+12345678901", testLocation);

        when(customerService.updateCustomer(eq(99L), any(CustomerDTO.class)))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        // When/Then
        mockMvc.perform(put("/api/customers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).updateCustomer(eq(99L), any(CustomerDTO.class));
    }

    @Test
    void testUpdateCustomer_ValidationError() throws Exception {
        // Invalid DTO - missing required name
        CustomerDTO invalidDTO = new CustomerDTO();
        invalidDTO.setEmail("john@example.com");
        invalidDTO.setPhoneNumber("+12345678901");

        // When/Then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).updateCustomer(anyLong(), any(CustomerDTO.class));
    }

    @Test
    void testDeleteCustomer_Success() throws Exception {
        // Given
        doNothing().when(customerService).deleteCustomer(anyLong());

        // When/Then
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(anyLong());
    }

    @Test
    void testDeleteCustomer_NotFound() throws Exception {
        // Given
        doThrow(new CustomerNotFoundException("Customer not found"))
                .when(customerService).deleteCustomer(anyLong());

        // When/Then
        mockMvc.perform(delete("/api/customers/99"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).deleteCustomer(anyLong());
    }

    @Test
    void testGetCustomerByEmail_Success() throws Exception {
        // Given
        when(customerService.getCustomerByEmail(anyString())).thenReturn(testCustomerDTO);

        // When/Then
        mockMvc.perform(get("/api/customers/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(customerService, times(1)).getCustomerByEmail(anyString());
    }

    @Test
    void testGetCustomerByEmail_NotFound() throws Exception {
        // Given
        when(customerService.getCustomerByEmail(anyString()))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        // When/Then
        mockMvc.perform(get("/api/customers/email/notfound@example.com"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerByEmail(anyString());
    }

    @Test
    void testSearchCustomers_Success() throws Exception {
        // Given
        CustomerDTO customer2 = new CustomerDTO(2L, "Johnny Smith", "johnny@example.com", "+12345678901", testLocation);
        List<CustomerDTO> searchResults = Arrays.asList(testCustomerDTO, customer2);

        when(customerService.searchCustomers(anyString())).thenReturn(searchResults);

        // When/Then
        mockMvc.perform(get("/api/customers/search")
                        .param("query", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Johnny Smith")));

        verify(customerService, times(1)).searchCustomers(anyString());
    }

    @Test
    void testValidateCustomer_Valid() throws Exception {
        // Given
        when(customerService.validateCustomer(anyLong())).thenReturn(true);

        // When/Then
        mockMvc.perform(get("/api/customers/1/validate"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(customerService, times(1)).validateCustomer(anyLong());
    }

    @Test
    void testValidateCustomer_Invalid() throws Exception {
        // Given
        when(customerService.validateCustomer(anyLong())).thenReturn(false);

        // When/Then
        mockMvc.perform(get("/api/customers/99/validate"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(customerService, times(1)).validateCustomer(anyLong());
    }

    @Test
    void testGetCustomerLocation_Success() throws Exception {
        // Given
        when(customerService.getCustomerLocation(anyLong())).thenReturn(testLocation);

        // When/Then
        mockMvc.perform(get("/api/customers/1/location"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude", is(40.7128)))
                .andExpect(jsonPath("$.longitude", is(-74.0060)));

        verify(customerService, times(1)).getCustomerLocation(anyLong());
    }

    @Test
    void testGetCustomerLocation_NotFound() throws Exception {
        // Given
        when(customerService.getCustomerLocation(anyLong()))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        // When/Then
        mockMvc.perform(get("/api/customers/99/location"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerLocation(anyLong());
    }

    @Test
    void testUpdateCustomerLocation_Success() throws Exception {
        // Given
        Location newLocation = new Location(38.8951, -77.0364);

        when(customerService.updateCustomerLocation(eq(1L), any(Location.class)))
                .thenReturn(testCustomerDTO);

        // When/Then
        mockMvc.perform(put("/api/customers/1/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")));

        verify(customerService, times(1)).updateCustomerLocation(eq(1L), any(Location.class));
    }

    @Test
    void testUpdateCustomerLocation_NotFound() throws Exception {
        // Given
        Location newLocation = new Location(38.8951, -77.0364);

        when(customerService.updateCustomerLocation(eq(99L), any(Location.class)))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        // When/Then
        mockMvc.perform(put("/api/customers/99/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).updateCustomerLocation(eq(99L), any(Location.class));
    }
}