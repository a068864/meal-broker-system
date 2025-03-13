package com.mealbroker.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealbroker.restaurant.dto.RestaurantDTO;
import com.mealbroker.restaurant.exception.RestaurantNotFoundException;
import com.mealbroker.restaurant.service.RestaurantService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantService restaurantService;

    private RestaurantDTO testRestaurantDTO;
    private List<RestaurantDTO> restaurantDTOList;

    @BeforeEach
    void setUp() {
        // Create test data
        testRestaurantDTO = new RestaurantDTO(1L, "Test Restaurant", "Italian");

        RestaurantDTO restaurant2 = new RestaurantDTO(2L, "Another Restaurant", "Chinese");
        restaurantDTOList = Arrays.asList(testRestaurantDTO, restaurant2);
    }

    @Test
    void testCreateRestaurant_Success() throws Exception {
        // Given
        when(restaurantService.createRestaurant(any(RestaurantDTO.class))).thenReturn(testRestaurantDTO);

        // When/Then
        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRestaurantDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.name", is("Test Restaurant")))
                .andExpect(jsonPath("$.cuisine", is("Italian")));

        verify(restaurantService, times(1)).createRestaurant(any(RestaurantDTO.class));
    }

    @Test
    void testCreateRestaurant_ValidationError() throws Exception {
        // Invalid DTO - missing required name
        RestaurantDTO invalidDTO = new RestaurantDTO();
        invalidDTO.setCuisine("Italian");

        // When/Then
        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(restaurantService, never()).createRestaurant(any(RestaurantDTO.class));
    }

    @Test
    void testGetRestaurant_Success() throws Exception {
        // Given
        when(restaurantService.getRestaurant(1L)).thenReturn(testRestaurantDTO);

        // When/Then
        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.name", is("Test Restaurant")))
                .andExpect(jsonPath("$.cuisine", is("Italian")));

        verify(restaurantService, times(1)).getRestaurant(1L);
    }

    @Test
    void testGetRestaurant_NotFound() throws Exception {
        // Given
        when(restaurantService.getRestaurant(99L)).thenThrow(new RestaurantNotFoundException("Restaurant not found with ID: 99"));

        // When/Then
        mockMvc.perform(get("/api/restaurants/99"))
                .andExpect(status().isNotFound());

        verify(restaurantService, times(1)).getRestaurant(99L);
    }

    @Test
    void testGetAllRestaurants_Success() throws Exception {
        // Given
        when(restaurantService.getAllRestaurants()).thenReturn(restaurantDTOList);

        // When/Then
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].restaurantId", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Restaurant")))
                .andExpect(jsonPath("$[1].restaurantId", is(2)))
                .andExpect(jsonPath("$[1].name", is("Another Restaurant")));

        verify(restaurantService, times(1)).getAllRestaurants();
    }

    @Test
    void testGetRestaurantsByCuisine_Success() throws Exception {
        // Given
        String cuisine = "Italian";
        when(restaurantService.getRestaurantsByCuisine(cuisine)).thenReturn(List.of(testRestaurantDTO));

        // When/Then
        mockMvc.perform(get("/api/restaurants/cuisine/Italian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].restaurantId", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Restaurant")))
                .andExpect(jsonPath("$[0].cuisine", is("Italian")));

        verify(restaurantService, times(1)).getRestaurantsByCuisine(cuisine);
    }

    @Test
    void testUpdateRestaurant_Success() throws Exception {
        // Given
        RestaurantDTO updateDTO = new RestaurantDTO(1L, "Updated Restaurant", "French");
        when(restaurantService.updateRestaurant(eq(1L), any(RestaurantDTO.class))).thenReturn(updateDTO);

        // When/Then
        mockMvc.perform(put("/api/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Restaurant")))
                .andExpect(jsonPath("$.cuisine", is("French")));

        verify(restaurantService, times(1)).updateRestaurant(eq(1L), any(RestaurantDTO.class));
    }

    @Test
    void testUpdateRestaurant_NotFound() throws Exception {
        // Given
        RestaurantDTO updateDTO = new RestaurantDTO(99L, "Updated Restaurant", "French");
        when(restaurantService.updateRestaurant(eq(99L), any(RestaurantDTO.class)))
                .thenThrow(new RestaurantNotFoundException("Restaurant not found with ID: 99"));

        // When/Then
        mockMvc.perform(put("/api/restaurants/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(restaurantService, times(1)).updateRestaurant(eq(99L), any(RestaurantDTO.class));
    }

    @Test
    void testUpdateRestaurant_ValidationError() throws Exception {
        // Invalid DTO - missing required name
        RestaurantDTO invalidDTO = new RestaurantDTO();
        invalidDTO.setCuisine("French");

        // When/Then
        mockMvc.perform(put("/api/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(restaurantService, never()).updateRestaurant(anyLong(), any(RestaurantDTO.class));
    }

    @Test
    void testDeleteRestaurant_Success() throws Exception {
        // Given
        doNothing().when(restaurantService).deleteRestaurant(1L);

        // When/Then
        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isNoContent());

        verify(restaurantService, times(1)).deleteRestaurant(1L);
    }

    @Test
    void testDeleteRestaurant_NotFound() throws Exception {
        // Given
        doThrow(new RestaurantNotFoundException("Restaurant not found with ID: 99"))
                .when(restaurantService).deleteRestaurant(99L);

        // When/Then
        mockMvc.perform(delete("/api/restaurants/99"))
                .andExpect(status().isNotFound());

        verify(restaurantService, times(1)).deleteRestaurant(99L);
    }
}