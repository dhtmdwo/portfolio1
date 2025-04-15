package com.example.be12fin5verdosewmthisbe.inventory.repository;

import com.example.be12fin5verdosewmthisbe.inventory.model.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

}