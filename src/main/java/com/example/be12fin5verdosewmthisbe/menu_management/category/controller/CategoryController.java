package com.example.be12fin5verdosewmthisbe.menu_management.category.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.dto.CategoryDto;
import com.example.be12fin5verdosewmthisbe.menu_management.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Category API", description = "메뉴 카테고리 관련 API")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 등록", description = "새로운 메뉴 카테고리를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 등록 성공"),
            @ApiResponse(responseCode = "5004", description = "이미 존재하는 카테고리입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/register")
    public BaseResponse<String> registerCategory(@RequestBody CategoryDto.requestDto dto) {
        Category existing = categoryService.findByName(dto.getName());
        if (existing != null) {
            return BaseResponse.error(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        Category category = Category.builder().name(dto.getName()).build();
        categoryService.register(category);
        return BaseResponse.success("Category registered successfully");
    }

    @Operation(summary = "카테고리 수정", description = "기존 메뉴 카테고리 이름을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
            @ApiResponse(responseCode = "5002", description = "해당 카테고리를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/update")
    public BaseResponse<String> updateCategory(@RequestBody CategoryDto.updateDto dto) {
        categoryService.update(dto.getOldName(), dto.getNewName());
        return BaseResponse.success("Category updated successfully");
    }

    @Operation(summary = "카테고리 삭제", description = "기존 메뉴 카테고리를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "5002", description = "해당 카테고리를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/delete")
    public BaseResponse<String> deleteCategory(@RequestBody CategoryDto.requestDto dto) {
        Category category = categoryService.findByName(dto.getName());
        categoryService.delete(category);
        return BaseResponse.success("Category deleted successfully");
    }

    @Operation(summary = "카테고리 목록 조회", description = "등록된 모든 메뉴 카테고리 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공"),
            @ApiResponse(responseCode = "5003", description = "카테고리 목록이 비어있습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/getList")
    public BaseResponse<List<CategoryDto.responseDto>> getCategoryList() {
        List<Category> categoryList = categoryService.findAll();
        if (categoryList.isEmpty()) {
            return BaseResponse.error(ErrorCode.EMPTY);
        }
        List<CategoryDto.responseDto> responseDtoList = categoryList.stream()
                .map(CategoryDto.responseDto::from)
                .collect(Collectors.toList());
        return BaseResponse.success(responseDtoList);
    }

    @Operation(summary = "카테고리 상세 조회", description = "특정 이름의 메뉴 카테고리 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 상세 정보 조회 성공"),
            @ApiResponse(responseCode = "5001", description = "카테고리 타입에 맞지 않는 잘못된 요청입니다."),
            @ApiResponse(responseCode = "5002", description = "해당 카테고리를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/detail")
    public BaseResponse<CategoryDto.responseDto> getCategoryDetail(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return BaseResponse.error(ErrorCode.INVAILD_REQUEST);
        }
        Category category = categoryService.findByName(name);
        return BaseResponse.success(CategoryDto.responseDto.from(category));
    }

    @Operation(summary = "카테고리 이름 검색", description = "이름 일부에 해당하는 카테고리를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 검색 성공"),
            @ApiResponse(responseCode = "5001", description = "카테고리 타입에 맞지 않는 잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/search")
    public BaseResponse<List<CategoryDto.responseDto>> searchCategory(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return BaseResponse.error(ErrorCode.INVAILD_REQUEST);
        }

        List<Category> result = categoryService.searchByName(keyword);
        List<CategoryDto.responseDto> response = result.stream()
                .map(CategoryDto.responseDto::from)
                .collect(Collectors.toList());
        return BaseResponse.success(response);
    }

}
