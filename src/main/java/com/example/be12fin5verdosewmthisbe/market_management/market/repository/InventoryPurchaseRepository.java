package com.example.be12fin5verdosewmthisbe.market_management.market.repository;

import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventoryPurchase;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface InventoryPurchaseRepository extends JpaRepository<InventoryPurchase, Long> {
    public List<InventoryPurchase> findInventoryPurchaseByStore(Store store);

    @Query("""
        SELECT DISTINCT ip FROM InventoryPurchase ip
        JOIN FETCH ip.inventorySale is
        JOIN FETCH ip.store s
        JOIN FETCH is.storeInventory si
        WHERE s.id = :storeId
        AND ip.status = :status
        AND is.createdAt >= :start
        AND is.createdAt <= :end
    """)
    List<InventoryPurchase> findMarketPurchaseForInventoryByStoreAndPeriod(
            @Param("storeId") Long storeId,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            @Param("status") InventoryPurchase.purchaseStatus purchaseStatus
    );

}
        