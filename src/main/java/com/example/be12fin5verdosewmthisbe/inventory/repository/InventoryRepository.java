package com.example.be12fin5verdosewmthisbe.inventory.repository;

import com.example.be12fin5verdosewmthisbe.inventory.model.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // 유통기한 기준 오름차순 정렬된 리스트 반환
    List<Inventory> findByStoreInventory_StoreinventoryIdOrderByExpiryDateAsc(Long storeInventoryId);

    // 유통기한이 가장 짧은 것 하나만 반환
    Optional<Inventory> findTopByStoreInventory_StoreinventoryIdOrderByExpiryDateAsc(Long storeInventoryId);

}
