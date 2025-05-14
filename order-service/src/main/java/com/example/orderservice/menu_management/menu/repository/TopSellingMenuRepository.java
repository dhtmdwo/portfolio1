package com.example.orderservice.menu_management.menu.repository;

import com.example.orderservice.menu_management.menu.model.TopSellingMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopSellingMenuRepository extends JpaRepository<TopSellingMenu, Long> {
}
