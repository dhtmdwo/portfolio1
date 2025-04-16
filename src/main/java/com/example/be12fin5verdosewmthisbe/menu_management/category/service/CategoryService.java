package com.example.be12fin5verdosewmthisbe.menu_management.category.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.CategoryOption;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.dto.CategoryDto;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryOptionRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final OptionRepository optionRepository;
    private final CategoryOptionRepository categoryOptionRepository;

    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }


    public void register(CategoryDto.requestDto dto) {
        log.info("Registering category: {}", dto);
        Category category = Category.builder().name(dto.getName()).build();
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        List<Option> options = optionRepository.findAllById(dto.getOptionIds());
        log.info("Found {} options", options.size());
        log.info("{}",dto.getOptionIds());
        for (Option option : options) {
            CategoryOption categoryOption = CategoryOption.builder()
                    .category(category)
                    .option(option)
                    .build();
            category.addCategoryOption(categoryOption);
            log.info("add category option");
        }
        categoryRepository.save(category);
    }

    public void update(String oldName, String newName) {
        Category existing = categoryRepository.findByName(oldName)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // name이 중복되면 예외 던짐 (기존 id와 다른 경우만)
        Optional<Category> duplicate = categoryRepository.findByName(newName);
        if (duplicate.isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        existing.setName(newName);
        categoryRepository.save(existing);
    }

    public void delete(Category category) {
        Category existing = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(existing);
    }

    public Category findById(Long id) {
        return categoryRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public Category findByName(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        if (category.isEmpty()) {
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return category.get();
    }

    public List<Category> searchByName(String keyword) {
        return categoryRepository.findByNameContaining(keyword);
    }

}
