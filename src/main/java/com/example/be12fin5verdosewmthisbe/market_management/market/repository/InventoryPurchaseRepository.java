package com.example.be12fin5verdosewmthisbe.market_management.market.repository;

import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventoryPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryPurchaseRepository extends JpaRepository<InventoryPurchase, Long> {
    public List<InventoryPurchase> findInventoryPurchaseByBuyerStoreId(Long buyerStoreId);
}
        