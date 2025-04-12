package com.example.be12fin5verdosewmthisbe.menu_management.menu.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Recipe;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuRegistrationDto;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.MenuRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    // 기타 메뉴 관련 서비스 로직 (조회, 수정, 삭제 등) 추가 가능
}