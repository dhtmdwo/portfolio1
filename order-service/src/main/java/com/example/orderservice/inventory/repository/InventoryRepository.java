package com.example.orderservice.inventory.repository;

import com.example.orderservice.inventory.model.Inventory;
import com.example.orderservice.inventory.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findAllByStoreInventory(StoreInventory storeInventory);
}
