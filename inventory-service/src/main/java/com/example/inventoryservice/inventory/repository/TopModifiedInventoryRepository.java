package com.example.inventoryservice.inventory.repository;

import com.example.inventoryservice.inventory.model.TopModifiedInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopModifiedInventoryRepository extends JpaRepository<TopModifiedInventory, Long> {
}
