package com.example.marketservice.market.repository;

import com.example.marketservice.market.model.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StoreInventoryRepository extends JpaRepository<StoreInventory, Long> {

}

