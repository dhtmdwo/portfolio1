package com.example.be12fin5verdosewmthisbe.menu_management.category.repository;

import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Your code here
    public Optional<Category> findByName(String name);

    List<Category> findByNameContaining(String keyword);
}
        