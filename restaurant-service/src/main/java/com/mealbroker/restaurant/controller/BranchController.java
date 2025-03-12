package com.mealbroker.restaurant.controller;

import com.mealbroker.domain.Location;
import com.mealbroker.restaurant.dto.BranchDTO;
import com.mealbroker.restaurant.dto.MenuItemDTO;
import com.mealbroker.restaurant.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for branch operations
 */
@RestController
@RequestMapping("/api/restaurants")
public class BranchController {

    private final BranchService branchService;

    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping("/{restaurantId}/branches")
    public ResponseEntity<BranchDTO> createBranch(
            @PathVariable Long restaurantId,
            @Valid @RequestBody BranchDTO branchDTO) {
        BranchDTO createdBranch = branchService.createBranch(restaurantId, branchDTO);
        return new ResponseEntity<>(createdBranch, HttpStatus.CREATED);
    }

    @GetMapping("/branches/{branchId}")
    public ResponseEntity<BranchDTO> getBranch(@PathVariable Long branchId) {
        BranchDTO branch = branchService.getBranch(branchId);
        return ResponseEntity.ok(branch);
    }

    @GetMapping("/{restaurantId}/branches")
    public ResponseEntity<List<BranchDTO>> getBranchesByRestaurant(@PathVariable Long restaurantId) {
        List<BranchDTO> branches = branchService.getBranchesByRestaurant(restaurantId);
        return ResponseEntity.ok(branches);
    }

    @GetMapping("/{restaurantId}/branches/active")
    public ResponseEntity<List<BranchDTO>> getActiveBranchesByRestaurant(@PathVariable Long restaurantId) {
        List<BranchDTO> branches = branchService.getActiveBranchesByRestaurant(restaurantId);
        return ResponseEntity.ok(branches);
    }

    @GetMapping("/{restaurantId}/branches/nearby")
    public ResponseEntity<List<BranchDTO>> findNearbyBranches(
            @PathVariable Long restaurantId,
            @RequestBody Location location,
            @RequestParam(defaultValue = "10.0") double maxDistance) {
        List<BranchDTO> branches = branchService.findNearbyBranches(restaurantId, location, maxDistance);
        return ResponseEntity.ok(branches);
    }

    @PutMapping("/branches/{branchId}")
    public ResponseEntity<BranchDTO> updateBranch(
            @PathVariable Long branchId,
            @Valid @RequestBody BranchDTO branchDTO) {
        BranchDTO updatedBranch = branchService.updateBranch(branchId, branchDTO);
        return ResponseEntity.ok(updatedBranch);
    }

    @DeleteMapping("/branches/{branchId}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long branchId) {
        branchService.deleteBranch(branchId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/branches/{branchId}/status")
    public ResponseEntity<BranchDTO> updateBranchStatus(
            @PathVariable Long branchId,
            @RequestParam boolean active) {
        BranchDTO updatedBranch = branchService.updateBranchStatus(branchId, active);
        return ResponseEntity.ok(updatedBranch);
    }

    @PostMapping("/branches/{branchId}/check-availability")
    public ResponseEntity<Boolean> checkItemsAvailability(
            @PathVariable Long branchId,
            @RequestBody List<MenuItemDTO> items) {
        boolean allAvailable = branchService.checkItemsAvailability(branchId, items);
        return ResponseEntity.ok(allAvailable);
    }
}