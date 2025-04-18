package com.example.be12fin5verdosewmthisbe.menu_management.category.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.CategoryOption;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.dto.CategoryDto;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryOptionRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionRepository;
import com.example.be12fin5verdosewmthisbe.store.model.Store;
import com.example.be12fin5verdosewmthisbe.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final OptionRepository optionRepository;
    private final StoreRepository storeRepository;

    public Page<CategoryDto.CategoryResponseDto> getCategoryList(Pageable pageable, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll(pageable)
                    .map(CategoryDto.CategoryResponseDto::fromEntity);
        } else {
            return categoryRepository.findByNameContaining(keyword, pageable)
                    .map(CategoryDto.CategoryResponseDto::fromEntity);
        }
    }
    public void register(CategoryDto.requestDto dto,Long storeId) {
        log.info("Registering category: {}", dto);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));
        Category category = Category.builder()
                .name(dto.getName())
                .store(store)
                .build();

        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        List<Option> options = optionRepository.findAllById(dto.getOptionIds());
        for (Option option : options) {
            CategoryOption categoryOption = CategoryOption.builder()
                    .category(category)
                    .option(option)
                    .build();
            category.addCategoryOption(categoryOption);
        }
        categoryRepository.save(category);
    }

    @Transactional
    public void update(Long id, String newName, List<Long> optionIds) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 이름 중복 검사
       /* Optional<Category> duplicate = categoryRepository.findByName(newName);
        if (duplicate.isPresent() && !duplicate.get().getId().equals(category.getId())) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }*/

        category.setName(newName);

        // 기존 옵션 관계 전부 제거
        category.getCategoryOptions().clear();
        log.info(optionIds.toString());
        List<Option> options = optionRepository.findAllById(optionIds);
        for (Option option : options) {
            CategoryOption categoryOption = CategoryOption.builder()
                    .category(category)
                    .option(option)
                    .build();
            category.addCategoryOption(categoryOption);
        }

        // 저장
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 연관된 메뉴들의 category를 null로 설정
        List<Menu> menus = category.getMenus();
        for (Menu menu : menus) {
            menu.setCategory(null);
        }

        categoryRepository.delete(category);
    }

    public Category findById(Long id) {
        return categoryRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

}
