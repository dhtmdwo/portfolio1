package com.example.orderservice.inventory.repository;

import com.example.orderservice.inventory.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreInventoryRepository extends JpaRepository<StoreInventory, Long> {
    List<StoreInventory> findByStoreId(Long storeId);
}
