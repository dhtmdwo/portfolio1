package com.example.be12fin5verdosewmthisbe.menu_management.menu.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import com.example.be12fin5verdosewmthisbe.inventory.repository.StoreInventoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Recipe;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuDto;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuRegisterDto;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.MenuRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    @Transactional
    public void registerMenu(MenuRegisterDto.MenuCreateRequestDto dto) {
        // 1. 카테고리 조회
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 2. 메뉴 생성
        Menu menu = Menu.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .category(category)
                .build();

        menuRepository.save(menu);

        // 3. 재료(Recipe) 연결
        List<Recipe> recipes = dto.getIngredients().stream().map(ingredientDto -> {
            StoreInventory storeInventory = storeInventoryRepository.findById(ingredientDto.getStoreInventoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.STORE_INVENTORY_NOT_FOUND));

            return Recipe.builder()
                    .menu(menu)
                    .storeInventory(storeInventory)
                    .quantity(ingredientDto.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        recipeRepository.saveAll(recipes);
    }
    public Menu findById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
    }
    public Page<Menu> searchMenusByName(String keyword, Pageable pageable) {
        Page<Menu> result = menuRepository.findByNameContaining(keyword, pageable);

        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.MENU_NOT_FOUND);
        }

        return result;
    }

    public Page<MenuDto.MenuListResponseDto> findAllMenus(Pageable pageable) {
        Page<Menu> result = menuRepository.findAll(pageable);

        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.MENU_NOT_FOUND);
        }

        return result.map(this::convertToMenuListResponseDto);
    }
    private MenuDto.MenuListResponseDto convertToMenuListResponseDto(Menu menu) {
        List<Recipe> recipes = menu.getRecipes();

        if (recipes.isEmpty()) {
            return MenuDto.MenuListResponseDto.builder()
                    .name(menu.getName())
                    .category(menu.getCategory().getName()) // Category도 name만 반환
                    .ingredients("재료 없음")
                    .build();
        }

        // 사용량이 가장 큰 재료 찾기
        Recipe maxRecipe = recipes.stream()
                .filter(r -> r.getStoreInventory() != null && r.getQuantity() != null)
                .max(Comparator.comparing(Recipe::getQuantity))
                .orElse(null);

        String ingredientSummary = "재료 정보 없음";

        if (maxRecipe != null) {
            String name = maxRecipe.getStoreInventory().getName();
            BigDecimal quantity = maxRecipe.getQuantity();
            String unit = maxRecipe.getStoreInventory().getUnit();

            int otherCount = (int) recipes.stream()
                    .filter(r -> r.getStoreInventory() != null)
                    .map(r -> r.getStoreInventory().getName())
                    .distinct()
                    .count() - 1;

            ingredientSummary = String.format("%s %s%s 외 %d종", name, quantity.stripTrailingZeros().toPlainString(), unit, otherCount);
        }

        return MenuDto.MenuListResponseDto.builder()
                .name(menu.getName())
                .category(menu.getCategory().getName())
                .ingredients(ingredientSummary)
                .build();
    }
    public void deleteMenu(Long menuId) {
        Menu existingMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
        menuRepository.delete(existingMenu);
    }

}