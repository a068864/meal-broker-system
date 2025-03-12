package com.mealbroker.restaurant.repository;

import com.mealbroker.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Menu entity
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    /**
     * Find menu by branch ID
     *
     * @param branchId the branch ID to search for
     * @return Menu for the branch
     */
    Menu findByBranchBranchId(Long branchId);
}