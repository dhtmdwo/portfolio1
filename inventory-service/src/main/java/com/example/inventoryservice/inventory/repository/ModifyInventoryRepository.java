package com.example.inventoryservice.inventory.repository;

import com.example.inventoryservice.inventory.model.ModifyInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface ModifyInventoryRepository extends JpaRepository<ModifyInventory, Integer> {


    @Query("""
        SELECT DISTINCT mi FROM ModifyInventory mi
        JOIN FETCH mi.storeInventory si
        WHERE si.storeId = :storeId
        AND mi.modifyDate >= :start
        AND mi.modifyDate <= :end
    """)
    List<ModifyInventory> findUpdateListByStoreAndPeriod(
            @Param("storeId") Long storeId,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end
    );

    @Query("""
    SELECT mi
    FROM ModifyInventory mi
    WHERE mi.modifyDate BETWEEN :start AND :end
    AND mi.storeInventory.storeId IS NOT NULL
    ORDER BY mi.storeInventory.storeId ASC, ABS(mi.modifyRate) DESC
    """)
    List<ModifyInventory> findTopModifiedInventories(@Param("start") Timestamp start, @Param("end") Timestamp end);


    List<ModifyInventory> findByStoreInventory_IdIn(List<Long> storeInventoryIds);
}
