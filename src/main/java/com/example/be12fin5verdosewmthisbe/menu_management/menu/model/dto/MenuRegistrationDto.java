package com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class MenuRegistrationDto {

    @Schema(description = "메뉴 등록 요청 DTO")
    @Getter
    @Setter
    public static class RequestDto {
        @Schema(description = "메뉴 이름", example = "비빔밥")
        private String name;

        @Schema(description = "메뉴 가격", example = "9000")
        private int price;

        @Schema(description = "카테고리 ID", example = "2")
        private Long categoryId;

        @Schema(description = "레시피 정보 목록")
        private List<RecipeInfoDto> recipes;
    }

    @Schema(description = "레시피 정보 DTO")
    @Getter
    @Setter
    public static class RecipeInfoDto {
        @Schema(description = "재고 ID", example = "ingredient-1")
        private String inventoryId;

        @Schema(description = "사용량", example = "100.0")
        private BigDecimal quantity;
    }
}