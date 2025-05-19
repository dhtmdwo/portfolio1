package com.example.orderservice.menu_management.menu.repository;

import com.example.orderservice.menu_management.menu.model.Menu;
import com.example.orderservice.menu_management.menu.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByMenu(Menu menu);

    List<Recipe> findAllByStoreInventoryId(Long storeInventoryId);


    @Query("""
      SELECT r
      FROM Recipe r
      JOIN FETCH r.menu m
      JOIN FETCH r.storeInventory si
      WHERE si.id = :storeInventoryId
    """)
    List<Recipe> findAllByStoreInventoryWithMenu(@Param("storeInventoryId") Long storeInventoryId);
}
