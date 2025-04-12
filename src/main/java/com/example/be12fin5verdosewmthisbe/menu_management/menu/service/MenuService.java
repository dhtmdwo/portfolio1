package com.example.be12fin5verdosewmthisbe.menu_management.menu.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Recipe;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuRegistrationDto;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuUpdateDto;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.MenuRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;

    public Menu registerMenu(MenuRegistrationDto.RequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Menu menu = Menu.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .category(category)
                .build();

        Menu savedMenu = menuRepository.save(menu);

        List<Recipe> recipes = requestDto.getRecipes().stream()
                .map(recipeDto -> Recipe.builder()
                        .menu(savedMenu)
                        .inventoryId(recipeDto.getInventoryId())
                        .price(recipeDto.getQuantity()) // 사용량을 price 필드에 저장 (요청대로)
                        .build())
                .collect(Collectors.toList());

        recipeRepository.saveAll(recipes);

        return savedMenu;
    }

    public Menu findById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
    }

    public Page<Menu> findAllMenus(Pageable pageable) {
        return menuRepository.findAll(pageable);
    }
    public void deleteMenu(Long menuId) {
        Menu existingMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
        menuRepository.delete(existingMenu);
    }

    public void updateMenu(Long menuId, MenuUpdateDto.RequestDto updateDto) {
        Menu existingMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        if (updateDto.getName() != null) {
            existingMenu.setName(updateDto.getName());
        }
        if (updateDto.getPrice() != null) {
            existingMenu.setPrice(updateDto.getPrice());
        }
        if (updateDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateDto.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            existingMenu.setCategory(category);
        }
        menuRepository.save(existingMenu);

        if (updateDto.getRecipes() != null && !updateDto.getRecipes().isEmpty()) {
            // 기존 레시피들을 Map으로 관리 (inventoryId -> Recipe)
            Map<String, Recipe> existingRecipeMap = existingMenu.getRecipes().stream()
                    .collect(Collectors.toMap(Recipe::getInventoryId, r -> r));

            List<Recipe> toSave = updateDto.getRecipes().stream()
                    .map(recipeDto -> {
                        Recipe existingRecipe = existingRecipeMap.get(recipeDto.getInventoryId());
                        if (existingRecipe != null) {
                            existingRecipe.setPrice(recipeDto.getQuantity()); // 사용량 업데이트
                            return existingRecipe;
                        } else {
                            return Recipe.builder()
                                    .menu(existingMenu)
                                    .inventoryId(recipeDto.getInventoryId())
                                    .price(recipeDto.getQuantity()) // 새로운 레시피 추가
                                    .build();
                        }
                    })
                    .collect(Collectors.toList());

            recipeRepository.saveAll(toSave);

            // 삭제된 레시피 처리 (요청에 없는 기존 inventoryId)
            List<String> updatedInventoryIds = updateDto.getRecipes().stream()
                    .map(MenuUpdateDto.RecipeUpdateInfoDto::getInventoryId)
                    .collect(Collectors.toList());

            List<Recipe> recipesToDelete = existingMenu.getRecipes().stream()
                    .filter(recipe -> !updatedInventoryIds.contains(recipe.getInventoryId()))
                    .collect(Collectors.toList());

            recipeRepository.deleteAll(recipesToDelete);
        }
    }
}