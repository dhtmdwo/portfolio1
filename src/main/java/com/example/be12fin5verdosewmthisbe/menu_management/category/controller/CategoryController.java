package com.example.be12fin5verdosewmthisbe.menu_management.category.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.dto.CategoryDto;
import com.example.be12fin5verdosewmthisbe.menu_management.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/register")
    public BaseResponse<String> registerCategory(CategoryDto.requestDto dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .build();
        categoryService.register(category);
        return BaseResponse.success("Category registered successfully");
    }
    @PutMapping("/update")
    public BaseResponse<String> updateCategory(CategoryDto.updateDto dto) {
        Category oldcategory = categoryService.findByName(dto.getOldName());

        oldcategory.setName(dto.getNewName());
        categoryService.update(oldcategory);
        return BaseResponse.success("Category update successfully");
    }
    @DeleteMapping("/delete")
    public BaseResponse<String> deleteCategory(CategoryDto.requestDto dto) {
        Category category = categoryService.findByName(dto.getName());
        categoryService.delete(category);
        return BaseResponse.success("Category deleted successfully");
    }
    @GetMapping("/getList")
    public BaseResponse<List<CategoryDto.responseDto>> getCategoryList() {
        List<Category> categoryList = categoryService.findAll();
        List<CategoryDto.responseDto> responseDtoList = categoryList.stream()
                .map(CategoryDto.responseDto::from)
                .collect(Collectors.toList());

        return BaseResponse.success(responseDtoList);
    }
    @GetMapping("/detail")
    public BaseResponse<CategoryDto.responseDto> getCategoryDetail(@RequestParam String name) {
        Category category = categoryService.findByName(name);
        CategoryDto.responseDto responseDto = CategoryDto.responseDto.from(category);
        return BaseResponse.success(responseDto);
    }



}
        