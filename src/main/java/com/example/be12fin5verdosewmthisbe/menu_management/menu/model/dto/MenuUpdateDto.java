package com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class MenuUpdateDto {

    @Schema(description = "메뉴 수정 요청 DTO")
    @Getter
    @Setter
    public static class RequestDto {
        @Schema(description = "수정할 메뉴 ID", required = true, example = "1")
        private Long menuId;

        @Schema(description = "새로운 메뉴 이름", example = "돌솥비빔밥")
        private String name;

        @Schema(description = "새로운 메뉴 가격", example = "10000")
        private Integer price;

        @Schema(description = "새로운 카테고리 ID", example = "2")
        private Long categoryId;

        @Schema(description = "레시피 정보 업데이트 또는 추가 목록")
        private List<RecipeUpdateInfoDto> recipes;
    }

    @Schema(description = "레시피 정보 업데이트 DTO")
    @Getter
    @Setter
    public static class RecipeUpdateInfoDto {
        @Schema(description = "재고 ID", required = true, example = "ingredient-1")
        private String inventoryId;

        @Schema(description = "새로운 사용량", example = "120.0")
        private BigDecimal quantity;
    }
}