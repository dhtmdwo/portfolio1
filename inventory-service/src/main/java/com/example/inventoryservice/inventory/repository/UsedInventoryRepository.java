package com.example.inventoryservice.inventory.repository;

import com.example.inventoryservice.inventory.model.UsedInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
public interface UsedInventoryRepository extends JpaRepository<UsedInventory, Long> {
    @Query("""
            SELECT DISTINCT ui FROM UsedInventory ui
            JOIN FETCH ui.storeInventory si
            WHERE si.storeId = :storeId
            AND ui.usedDate >= :start
            AND ui.usedDate < :end
        """)
    List<UsedInventory> findUsedInventoryByStoreAndPeriod(
            @Param("storeId") Long storeId,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end
    );
}
