package com.example.be12fin5verdosewmthisbe.inventory.repository;

import com.example.be12fin5verdosewmthisbe.inventory.model.Inventory;
import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
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

    @Query("""
        SELECT DISTINCT si FROM StoreInventory si
        LEFT JOIN FETCH si.inventoryList i
        JOIN FETCH si.store s
        WHERE s.id = :storeId       
    """)
    List<StoreInventory> findAllStoreInventoryByStore(@Param("storeId") Long storeId);

}

