package com.example.be12fin5verdosewmthisbe.inventory.repository;

import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreInventoryRepository extends JpaRepository<StoreInventory, Long> {
    boolean existsByName(String name);

    List<StoreInventory> findByStore_Id(Long storeId);

    @Query("""
        SELECT DISTINCT sm FROM StoreInventory sm
        JOIN FETCH sm.store s
        WHERE s.id = :storeId       
    """)
    List<StoreInventory> findInventoryListByStore(@Param("storeId") Long storeId);



}

