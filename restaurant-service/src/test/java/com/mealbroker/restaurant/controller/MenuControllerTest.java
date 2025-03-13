package com.mealbroker.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealbroker.domain.dto.MenuDTO;
import com.mealbroker.domain.dto.MenuItemDTO;
import com.mealbroker.restaurant.exception.BranchNotFoundException;
import com.mealbroker.restaurant.exception.MenuItemNotFoundException;
import com.mealbroker.restaurant.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuController.class)
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuService menuService;

    private MenuDTO testMenuDTO;
    private MenuItemDTO testMenuItemDTO;
    private List<MenuItemDTO> menuItemDTOList;

    @BeforeEach
    void setUp() {
        // Create test data
        testMenuItemDTO = new MenuItemDTO(1L, "Pizza", "Delicious pizza", 10.99);
        testMenuItemDTO.setAvailable(true);
        testMenuItemDTO.setStock(10);

        MenuItemDTO item2 = new MenuItemDTO(2L, "Burger", "Tasty burger", 8.99);
        item2.setAvailable(true);
        item2.setStock(5);

        menuItemDTOList = Arrays.asList(testMenuItemDTO, item2);

        testMenuDTO = new MenuDTO(1L);
        testMenuDTO.setItems(menuItemDTOList);
    }

    @Test
    void testGetMenu_Success() throws Exception {
        // Given
        when(menuService.getMenu(1L)).thenReturn(testMenuDTO);

        // When/Then
        mockMvc.perform(get("/api/restaurants/branches/1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuId", is(1)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].menuItemId", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Pizza")))
                .andExpect(jsonPath("$.items[1].menuItemId", is(2)))
                .andExpect(jsonPath("$.items[1].name", is("Burger")));

        verify(menuService, times(1)).getMenu(1L);
    }

    @Test
    void testGetMenu_BranchNotFound() throws Exception {
        // Given
        when(menuService.getMenu(99L)).thenThrow(new BranchNotFoundException("Branch not found with ID: 99"));

        // When/Then
        mockMvc.perform(get("/api/restaurants/branches/99/menu"))
                .andExpect(status().isNotFound());

        verify(menuService, times(1)).getMenu(99L);
    }

    @Test
    void testAddMenuItem_Success() throws Exception {
        // Given
        testMenuItemDTO.setQuantity(1);
        when(menuService.addMenuItem(eq(1L), any(MenuItemDTO.class))).thenReturn(testMenuItemDTO);

        // When/Then
        mockMvc.perform(post("/api/restaurants/branches/1/menu/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMenuItemDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.menuItemId", is(1)))
                .andExpect(jsonPath("$.name", is("Pizza")))
                .andExpect(jsonPath("$.price", is(10.99)))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.stock", is(10)));

        verify(menuService, times(1)).addMenuItem(eq(1L), any(MenuItemDTO.class));
    }

    @Test
    void testAddMenuItem_ValidationError() throws Exception {
        // Invalid DTO - missing required fields
        MenuItemDTO invalidDTO = new MenuItemDTO();

        // When/Then
        mockMvc.perform(post("/api/restaurants/branches/1/menu/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(menuService, never()).addMenuItem(anyLong(), any(MenuItemDTO.class));
    }

    @Test
    void testAddMenuItem_BranchNotFound() throws Exception {
        // Given
        testMenuItemDTO.setQuantity(1);
        when(menuService.addMenuItem(eq(99L), any(MenuItemDTO.class)))
                .thenThrow(new BranchNotFoundException("Branch not found with ID: 99"));

        // When/Then
        mockMvc.perform(post("/api/restaurants/branches/99/menu/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMenuItemDTO)))
                .andExpect(status().isNotFound());

        verify(menuService, times(1)).addMenuItem(eq(99L), any(MenuItemDTO.class));
    }

    @Test
    void testUpdateMenuItem_Success() throws Exception {
        // Given
        MenuItemDTO updateDTO = new MenuItemDTO(1L, "Updated Pizza", "Updated description", 12.99);
        updateDTO.setAvailable(false);
        updateDTO.setStock(5);
        List<String> allergens = new ArrayList<>();
        allergens.add("Gluten");
        updateDTO.setQuantity(1);
        updateDTO.setAllergens(allergens);

        when(menuService.updateMenuItem(eq(1L), any(MenuItemDTO.class))).thenReturn(updateDTO);

        // When/Then
        mockMvc.perform(put("/api/restaurants/menu/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuItemId", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Pizza")))
                .andExpect(jsonPath("$.price", is(12.99)))
                .andExpect(jsonPath("$.available", is(false)))
                .andExpect(jsonPath("$.stock", is(5)));

        verify(menuService, times(1)).updateMenuItem(eq(1L), any(MenuItemDTO.class));
    }

    @Test
    void testUpdateMenuItem_NotFound() throws Exception {
        // Given
        testMenuItemDTO.setQuantity(1);
        when(menuService.updateMenuItem(eq(99L), any(MenuItemDTO.class)))
                .thenThrow(new MenuItemNotFoundException("Menu item not found with ID: 99"));

        // When/Then
        mockMvc.perform(put("/api/restaurants/menu/items/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMenuItemDTO)))
                .andExpect(status().isNotFound());

        verify(menuService, times(1)).updateMenuItem(eq(99L), any(MenuItemDTO.class));
    }

    @Test
    void testRemoveMenuItem_Success() throws Exception {
        // Given
        doNothing().when(menuService).removeMenuItem(1L);

        // When/Then
        mockMvc.perform(delete("/api/restaurants/menu/items/1"))
                .andExpect(status().isNoContent());

        verify(menuService, times(1)).removeMenuItem(1L);
    }

    @Test
    void testRemoveMenuItem_NotFound() throws Exception {
        // Given
        doThrow(new MenuItemNotFoundException("Menu item not found with ID: 99"))
                .when(menuService).removeMenuItem(99L);

        // When/Then
        mockMvc.perform(delete("/api/restaurants/menu/items/99"))
                .andExpect(status().isNotFound());

        verify(menuService, times(1)).removeMenuItem(99L);
    }

    @Test
    void testUpdateItemAvailability_Success() throws Exception {
        // Given
        when(menuService.updateItemAvailability(eq(1L), eq(false))).thenReturn(testMenuItemDTO);

        // When/Then
        mockMvc.perform(patch("/api/restaurants/menu/items/1/availability")
                        .param("available", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuItemId", is(1)));

        verify(menuService, times(1)).updateItemAvailability(eq(1L), eq(false));
    }

    @Test
    void testUpdateItemAvailability_NotFound() throws Exception {
        // Given
        when(menuService.updateItemAvailability(eq(99L), anyBoolean()))
                .thenThrow(new MenuItemNotFoundException("Menu item not found with ID: 99"));

        // When/Then
        mockMvc.perform(patch("/api/restaurants/menu/items/99/availability")
                        .param("available", "false"))
                .andExpect(status().isNotFound());

        verify(menuService, times(1)).updateItemAvailability(eq(99L), eq(false));
    }

    @Test
    void testUpdateItemStock_Success() throws Exception {
        // Given
        when(menuService.updateItemStock(eq(1L), eq(20))).thenReturn(testMenuItemDTO);

        // When/Then
        mockMvc.perform(patch("/api/restaurants/menu/items/1/stock")
                        .param("stock", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuItemId", is(1)));

        verify(menuService, times(1)).updateItemStock(eq(1L), eq(20));
    }

    @Test
    void testUpdateItemStock_NotFound() throws Exception {
        // Given
        when(menuService.updateItemStock(eq(99L), anyInt()))
                .thenThrow(new MenuItemNotFoundException("Menu item not found with ID: 99"));

        // When/Then
        mockMvc.perform(patch("/api/restaurants/menu/items/99/stock")
                        .param("stock", "20"))
                .andExpect(status().isNotFound());

        verify(menuService, times(1)).updateItemStock(eq(99L), eq(20));
    }

    @Test
    void testGetAvailableMenuItems_Success() throws Exception {
        // Given
        when(menuService.getAvailableMenuItems(1L)).thenReturn(menuItemDTOList);

        // When/Then
        mockMvc.perform(get("/api/restaurants/branches/1/menu/available-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].menuItemId", is(1)))
                .andExpect(jsonPath("$[0].name", is("Pizza")))
                .andExpect(jsonPath("$[1].menuItemId", is(2)))
                .andExpect(jsonPath("$[1].name", is("Burger")));

        verify(menuService, times(1)).getAvailableMenuItems(1L);
    }

    @Test
    void testGetAvailableMenuItems_BranchNotFound() throws Exception {
        // Given
        when(menuService.getAvailableMenuItems(99L))
                .thenThrow(new BranchNotFoundException("Branch not found with ID: 99"));

        // When/Then
        mockMvc.perform(get("/api/restaurants/branches/99/menu/available-items"))
                .andExpect(status().isNotFound());

        verify(menuService, times(1)).getAvailableMenuItems(99L);
    }
}