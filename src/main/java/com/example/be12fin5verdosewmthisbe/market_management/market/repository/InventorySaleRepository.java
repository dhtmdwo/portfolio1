package com.example.be12fin5verdosewmthisbe.market_management.market.repository;

import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventorySaleRepository extends JpaRepository<InventorySale, Integer> {
    // Your code here
}
        