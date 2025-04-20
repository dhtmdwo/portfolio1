package com.example.be12fin5verdosewmthisbe.menu_management.menu.repository;

import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Page<Menu> findByNameContaining(String keyword, Pageable pageable);

    @Query("""
        SELECT DISTINCT m FROM Menu m
        JOIN FETCH m.category c
        JOIN FETCH c.store s
        WHERE s.id = :storeId
    """)
    List<Menu> findMenuBystore(@Param("storeId") Long storeId);

}
