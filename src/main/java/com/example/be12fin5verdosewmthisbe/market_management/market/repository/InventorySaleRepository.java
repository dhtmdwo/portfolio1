package com.example.be12fin5verdosewmthisbe.market_management.market.repository;

import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventorySaleRepository extends JpaRepository<InventorySale, Long> {
    List<InventorySale> findBySellerStoreIdAndStatusIn(Long sellerStoreId, List<InventorySale.saleStatus> statuses);
    List<InventorySale> findBySellerStoreId(Long sellerStoreId);
}
        