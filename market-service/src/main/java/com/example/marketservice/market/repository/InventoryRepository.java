package com.example.marketservice.market.repository;

import com.example.marketservice.market.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 유통기한이 가장 짧은 것 하나만 반환
    Optional<Inventory> findTopByStoreInventory_IdOrderByExpiryDateAsc(Long storeInventoryId);

}
