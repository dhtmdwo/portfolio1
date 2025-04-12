package com.example.be12fin5verdosewmthisbe.menu_management.category.service;

import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    public Category register(Category category) {
        return categoryRepository.save(category);
    }
    public Category update(Category category) {
        return categoryRepository.save(category);
    }
    public void delete(Category category) {
        categoryRepository.delete(category);
    }


}
        