package com.example.be12fin5verdosewmthisbe.menu_management.category.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.dto.CategoryDto;
import com.example.be12fin5verdosewmthisbe.menu_management.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RestController("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 등록", description = "새로운 메뉴 카테고리를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 등록 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"Category registered successfully\", \"data\": null}"))),
            @ApiResponse(responseCode = "5001", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"잘못된 요청 형식입니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @PostMapping("/register")
    public BaseResponse<String> registerCategory(
            @Parameter(description = "등록할 카테고리 정보", required = true, schema = @Schema(implementation = CategoryDto.requestDto.class))
            @RequestBody CategoryDto.requestDto dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .build();
        categoryService.register(category);
        return BaseResponse.success("Category registered successfully");
    }

    @Operation(summary = "카테고리 수정", description = "기존 메뉴 카테고리 이름을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"Category update successfully\", \"data\": null}"))),
            @ApiResponse(responseCode = "5002", description = "수정할 카테고리를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 이름을 가진 카테고리를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "5001", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"잘못된 요청 형식입니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @PutMapping("/update")
    public BaseResponse<String> updateCategory(
            @Parameter(description = "수정할 카테고리 정보", required = true, schema = @Schema(implementation = CategoryDto.updateDto.class))
            @RequestBody CategoryDto.updateDto dto) {
        Category oldcategory = categoryService.findByName(dto.getOldName());
        if (oldcategory == null) {
            return BaseResponse.error(ErrorCode.NO_EXIST_NAME);
        }
        oldcategory.setName(dto.getNewName());
        categoryService.update(oldcategory);
        return BaseResponse.success("Category update successfully");
    }

    @Operation(summary = "카테고리 삭제", description = "기존 메뉴 카테고리를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"Category deleted successfully\", \"data\": null}"))),
            @ApiResponse(responseCode = "5002", description = "삭제할 카테고리를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 이름을 가진 카테고리를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "5001", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"잘못된 요청 형식입니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @DeleteMapping("/delete")
    public BaseResponse<String> deleteCategory(
            @Parameter(description = "삭제할 카테고리 정보", required = true, schema = @Schema(implementation = CategoryDto.requestDto.class))
            @RequestBody CategoryDto.requestDto dto) {
        Category category = categoryService.findByName(dto.getName());
        if (category == null) {
            return BaseResponse.error(ErrorCode.NO_EXIST_NAME);
        }
        categoryService.delete(category);
        return BaseResponse.success("Category deleted successfully");
    }

    @Operation(summary = "카테고리 목록 조회", description = "등록된 모든 메뉴 카테고리 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공",
            content = @Content(array = @ArraySchema(items = @Schema(implementation = CategoryDto.responseDto.class)))),
            @ApiResponse(responseCode = "5003", description = "등록된 카테고리가 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"등록된 카테고리가 없습니다.\", \"data\": []}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
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
            @ApiResponse(responseCode = "200", description = "카테고리 상세 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "5002", description = "해당 이름을 가진 카테고리를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 이름을 가진 카테고리를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "5001", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"카테고리 이름을 입력해주세요.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @GetMapping("/detail")
    public BaseResponse<CategoryDto.responseDto> getCategoryDetail(
            @Parameter(description = "조회할 카테고리 이름", required = true)
            @RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return BaseResponse.error(ErrorCode.INVAILD_REQUEST);
        }
        Category category = categoryService.findByName(name);
        if (category == null) {
            return BaseResponse.error(ErrorCode.NO_EXIST_NAME);
        }
        CategoryDto.responseDto responseDto = CategoryDto.responseDto.from(category);
        return BaseResponse.success(responseDto);
    }
}