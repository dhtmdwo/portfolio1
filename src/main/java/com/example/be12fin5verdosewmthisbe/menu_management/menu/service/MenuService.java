package com.example.be12fin5verdosewmthisbe.menu_management.menu.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Recipe;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuInfoDto;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuRegistrationDto;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuSaleDto;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuUpdateDto;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.MenuRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.RecipeRepository;
import com.example.be12fin5verdosewmthisbe.order.model.Order;
import com.example.be12fin5verdosewmthisbe.order.model.OrderMenu;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderMenuRepository;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final OrderMenuRepository orderMenuRepository;

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
                        .price(recipeDto.getQuantity())
                        .build())
                .collect(Collectors.toList());

        recipeRepository.saveAll(recipes);

        return savedMenu;
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

    public Page<Menu> findAllMenus(Pageable pageable) {
        Page<Menu> result = menuRepository.findAll(pageable);

        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.MENU_NOT_FOUND);
        }

        return result;
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
            List<String> inventoryIdList = updateDto.getRecipes().stream()
                    .map(MenuUpdateDto.RecipeUpdateInfoDto::getInventoryId)
                    .collect(Collectors.toList());

            if (inventoryIdList.size() != inventoryIdList.stream().distinct().count()) {
                throw new CustomException(ErrorCode.RECIPE_DUPLICATED_INVENTORY);
            }

            updateDto.getRecipes().forEach(recipeDto -> {
                if (recipeDto.getQuantity() == null || recipeDto.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
                    throw new CustomException(ErrorCode.RECIPE_QUANTITY_INVALID);
                }
            });

            Map<String, Recipe> existingRecipeMap = existingMenu.getRecipes().stream()
                    .collect(Collectors.toMap(Recipe::getInventoryId, r -> r));

            List<Recipe> toSave = updateDto.getRecipes().stream()
                    .map(recipeDto -> {
                        Recipe existingRecipe = existingRecipeMap.get(recipeDto.getInventoryId());
                        if (existingRecipe != null) {
                            existingRecipe.setPrice(recipeDto.getQuantity());
                            return existingRecipe;
                        } else {
                            return Recipe.builder()
                                    .menu(existingMenu)
                                    .inventoryId(recipeDto.getInventoryId())
                                    .price(recipeDto.getQuantity())
                                    .build();
                        }
                    })
                    .collect(Collectors.toList());

            recipeRepository.saveAll(toSave);

            List<String> updatedInventoryIds = inventoryIdList;
            List<Recipe> recipesToDelete = existingMenu.getRecipes().stream()
                    .filter(recipe -> !updatedInventoryIds.contains(recipe.getInventoryId()))
                    .collect(Collectors.toList());

            recipeRepository.deleteAll(recipesToDelete);
        }
    }

    public List<MenuInfoDto.MenuResponse> getmenuList(Long storeId) {

        List<Menu> menuList = menuRepository.findMenuBystore(storeId);
        List<MenuInfoDto.MenuResponse> menuResponseList = new ArrayList<>();

        for (Menu menu : menuList) {
            String menuName = menu.getName();
            String category = menu.getCategory().getName();
            MenuInfoDto.MenuResponse menuResponse = MenuInfoDto.MenuResponse.of(menuName, category);
            menuResponseList.add(menuResponse);
        }
        return(menuResponseList);
    }

    public List<MenuSaleDto.Response> getSaleList(Long storeId, MenuSaleDto.DateRequest dto) {

        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());


        List<OrderMenu> saleList = orderMenuRepository.findSaleMenusByStoreAndPeriod(storeId, startTimestamp, endTimestamp);
        List<MenuSaleDto.Response> menuSaleList = new ArrayList<>();

        for (OrderMenu orderMenu : saleList) {
            Timestamp date = orderMenu.getOrder().getCreatedAt();
            String category = orderMenu.getMenu().getCategory().getName();
            String menuName = orderMenu.getMenu().getName();
            int quantity = orderMenu.getQuantity();
            MenuSaleDto.Response menuSale = MenuSaleDto.Response.of(date, category, menuName, quantity);
            menuSaleList.add(menuSale);
        }
        return(menuSaleList);
    }


}