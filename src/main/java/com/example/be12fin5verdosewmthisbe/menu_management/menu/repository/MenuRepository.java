package com.example.be12fin5verdosewmthisbe.menu_management.menu.repository;

import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Page<Menu> findByNameContaining(String keyword, Pageable pageable);
}
