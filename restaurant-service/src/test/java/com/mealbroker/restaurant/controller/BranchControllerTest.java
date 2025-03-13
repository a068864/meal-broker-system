package com.mealbroker.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealbroker.domain.Location;
import com.mealbroker.restaurant.dto.BranchDTO;
import com.mealbroker.restaurant.dto.MenuItemDTO;
import com.mealbroker.restaurant.exception.BranchNotFoundException;
import com.mealbroker.restaurant.exception.RestaurantNotFoundException;
import com.mealbroker.restaurant.service.BranchService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BranchController.class)
public class BranchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BranchService branchService;

    private BranchDTO testBranchDTO;
    private List<BranchDTO> branchDTOList;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        // Create test data
        testLocation = new Location(40.7128, -74.0060);
        testBranchDTO = new BranchDTO(1L, "Downtown Branch", 1L, testLocation);
        testBranchDTO.setActive(true);

        BranchDTO branch2 = new BranchDTO(2L, "Uptown Branch", 1L, testLocation);
        branch2.setActive(true);

        branchDTOList = Arrays.asList(testBranchDTO, branch2);
    }

    @Test
    void testCreateBranch_Success() throws Exception {
        // Given
        when(branchService.createBranch(eq(1L), any(BranchDTO.class))).thenReturn(testBranchDTO);

        // When/Then
        mockMvc.perform(post("/api/restaurants/1/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBranchDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.branchId", is(1)))
                .andExpect(jsonPath("$.branchName", is("Downtown Branch")))
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.active", is(true)));

        verify(branchService, times(1)).createBranch(eq(1L), any(BranchDTO.class));
    }

    @Test
    void testCreateBranch_ValidationError() throws Exception {
        // Invalid DTO - missing required branch name
        BranchDTO invalidDTO = new BranchDTO();
        invalidDTO.setLocation(testLocation);

        // When/Then
        mockMvc.perform(post("/api/restaurants/1/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(branchService, never()).createBranch(anyLong(), any(BranchDTO.class));
    }

    @Test
    void testCreateBranch_RestaurantNotFound() throws Exception {
        // Given
        when(branchService.createBranch(eq(99L), any(BranchDTO.class)))
                .thenThrow(new RestaurantNotFoundException("Restaurant not found with ID: 99"));

        // When/Then
        mockMvc.perform(post("/api/restaurants/99/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBranchDTO)))
                .andExpect(status().isNotFound());

        verify(branchService, times(1)).createBranch(eq(99L), any(BranchDTO.class));
    }

    @Test
    void testGetBranch_Success() throws Exception {
        // Given
        when(branchService.getBranch(1L)).thenReturn(testBranchDTO);

        // When/Then
        mockMvc.perform(get("/api/restaurants/branches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchId", is(1)))
                .andExpect(jsonPath("$.branchName", is("Downtown Branch")))
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.active", is(true)));

        verify(branchService, times(1)).getBranch(1L);
    }

    @Test
    void testGetBranch_NotFound() throws Exception {
        // Given
        when(branchService.getBranch(99L)).thenThrow(new BranchNotFoundException("Branch not found with ID: 99"));

        // When/Then
        mockMvc.perform(get("/api/restaurants/branches/99"))
                .andExpect(status().isNotFound());

        verify(branchService, times(1)).getBranch(99L);
    }

    @Test
    void testGetBranchesByRestaurant_Success() throws Exception {
        // Given
        when(branchService.getBranchesByRestaurant(1L)).thenReturn(branchDTOList);

        // When/Then
        mockMvc.perform(get("/api/restaurants/1/branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].branchId", is(1)))
                .andExpect(jsonPath("$[0].branchName", is("Downtown Branch")))
                .andExpect(jsonPath("$[1].branchId", is(2)))
                .andExpect(jsonPath("$[1].branchName", is("Uptown Branch")));

        verify(branchService, times(1)).getBranchesByRestaurant(1L);
    }

    @Test
    void testGetBranchesByRestaurant_RestaurantNotFound() throws Exception {
        // Given
        when(branchService.getBranchesByRestaurant(99L))
                .thenThrow(new RestaurantNotFoundException("Restaurant not found with ID: 99"));

        // When/Then
        mockMvc.perform(get("/api/restaurants/99/branches"))
                .andExpect(status().isNotFound());

        verify(branchService, times(1)).getBranchesByRestaurant(99L);
    }

    @Test
    void testGetActiveBranchesByRestaurant_Success() throws Exception {
        // Given
        when(branchService.getActiveBranchesByRestaurant(1L)).thenReturn(branchDTOList);

        // When/Then
        mockMvc.perform(get("/api/restaurants/1/branches/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].branchId", is(1)))
                .andExpect(jsonPath("$[0].active", is(true)))
                .andExpect(jsonPath("$[1].branchId", is(2)))
                .andExpect(jsonPath("$[1].active", is(true)));

        verify(branchService, times(1)).getActiveBranchesByRestaurant(1L);
    }

    @Test
    void testUpdateBranch_Success() throws Exception {
        // Given
        BranchDTO updateDTO = new BranchDTO(1L, "Updated Branch", 1L, testLocation);
        updateDTO.setActive(false);

        when(branchService.updateBranch(eq(1L), any(BranchDTO.class))).thenReturn(updateDTO);

        // When/Then
        mockMvc.perform(put("/api/restaurants/branches/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchId", is(1)))
                .andExpect(jsonPath("$.branchName", is("Updated Branch")))
                .andExpect(jsonPath("$.active", is(false)));

        verify(branchService, times(1)).updateBranch(eq(1L), any(BranchDTO.class));
    }

    @Test
    void testUpdateBranch_NotFound() throws Exception {
        // Given
        BranchDTO updateDTO = new BranchDTO(99L, "Updated Branch", 1L, testLocation);

        when(branchService.updateBranch(eq(99L), any(BranchDTO.class)))
                .thenThrow(new BranchNotFoundException("Branch not found with ID: 99"));

        // When/Then
        mockMvc.perform(put("/api/restaurants/branches/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(branchService, times(1)).updateBranch(eq(99L), any(BranchDTO.class));
    }

    @Test
    void testDeleteBranch_Success() throws Exception {
        // Given
        doNothing().when(branchService).deleteBranch(1L);

        // When/Then
        mockMvc.perform(delete("/api/restaurants/branches/1"))
                .andExpect(status().isNoContent());

        verify(branchService, times(1)).deleteBranch(1L);
    }

    @Test
    void testDeleteBranch_NotFound() throws Exception {
        // Given
        doThrow(new BranchNotFoundException("Branch not found with ID: 99"))
                .when(branchService).deleteBranch(99L);

        // When/Then
        mockMvc.perform(delete("/api/restaurants/branches/99"))
                .andExpect(status().isNotFound());

        verify(branchService, times(1)).deleteBranch(99L);
    }

    @Test
    void testUpdateBranchStatus_Success() throws Exception {
        // Given
        when(branchService.updateBranchStatus(eq(1L), eq(false))).thenReturn(testBranchDTO);

        // When/Then
        mockMvc.perform(patch("/api/restaurants/branches/1/status")
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchId", is(1)));

        verify(branchService, times(1)).updateBranchStatus(eq(1L), eq(false));
    }

    @Test
    void testCheckItemsAvailability_AllAvailable() throws Exception {
        // Given
        List<MenuItemDTO> menuItems = List.of(
                new MenuItemDTO(1L, 2) // Menu item ID 1, quantity 2
        );

        when(branchService.checkItemsAvailability(eq(1L), any())).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/api/restaurants/branches/1/check-availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuItems)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(branchService, times(1)).checkItemsAvailability(eq(1L), any());
    }

    @Test
    void testCheckItemsAvailability_NotAllAvailable() throws Exception {
        // Given
        List<MenuItemDTO> menuItems = List.of(
                new MenuItemDTO(1L, 2) // Menu item ID 1, quantity 2
        );

        when(branchService.checkItemsAvailability(eq(1L), any())).thenReturn(false);

        // When/Then
        mockMvc.perform(post("/api/restaurants/branches/1/check-availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuItems)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(branchService, times(1)).checkItemsAvailability(eq(1L), any());
    }

    @Test
    void testCheckItemsAvailability_BranchNotFound() throws Exception {
        // Given
        List<MenuItemDTO> menuItems = List.of(
                new MenuItemDTO(1L, 2) // Menu item ID 1, quantity 2
        );

        when(branchService.checkItemsAvailability(eq(99L), any()))
                .thenThrow(new BranchNotFoundException("Branch not found with ID: 99"));

        // When/Then
        mockMvc.perform(post("/api/restaurants/branches/99/check-availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuItems)))
                .andExpect(status().isNotFound());

        verify(branchService, times(1)).checkItemsAvailability(eq(99L), any());
    }
}