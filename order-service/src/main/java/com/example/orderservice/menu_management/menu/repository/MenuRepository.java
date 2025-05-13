package com.example.orderservice.menu_management.menu.repository;

import com.example.orderservice.menu_management.menu.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {



    @Query("""
SELECT DISTINCT m FROM Menu m
LEFT JOIN FETCH m.recipeList r
WHERE m.id IN :ids
""")
    List<Menu> load(@Param("ids") List<Long> ids);


    @Query("""
        SELECT DISTINCT m FROM Menu m
        JOIN FETCH m.category c
        WHERE m.storeId = :storeId
    """)
    List<Menu> findMenuByStoreId(@Param("storeId") Long storeId);

    @EntityGraph(attributePaths = {
            "recipeList",
            "recipeList.storeInventory"
    })
    Page<Menu> findByStoreId(Long storeId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "recipeList",
            "recipeList.storeInventory"
    })
    Page<Menu> findByStoreIdAndNameContaining(Long storeId, String keyword, Pageable pageable);


    Optional<Menu> findByStoreIdAndName(Long attr2, String name);

    List<Menu> findAllByStoreId(Long storeId);

    @Query("SELECT m FROM Menu m " +
            "LEFT JOIN FETCH m.recipeList r " +
            "WHERE m.id = :menuId")
    Optional<Menu> findMenuWithRecipesAndInventories(@Param("menuId") Long menuId);
}
