package com.example.be12fin5verdosewmthisbe.menu_management.category.model.dto;

import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CategoryDto {

    @Schema(description = "카테고리 등록 요청 DTO")
    @Getter
    public static class requestDto {
        @Schema(description = "카테고리 이름", example = "한식")
        private String name;
    }

    @Schema(description = "카테고리 수정 요청 DTO")
    @Getter
    public static class updateDto {
        @Schema(description = "기존 카테고리 이름", example = "중식")
        private String oldName;
        @Schema(description = "새로운 카테고리 이름", example = "중국음식")
        private String newName;
    }

    @Schema(description = "카테고리 응답 DTO")
    @Getter
    @Setter
    @AllArgsConstructor
    public static class responseDto {
        @Schema(description = "카테고리 이름", example = "양식")
        private String name;

        public static responseDto from(Category category) {
            return new responseDto(category.getName());
        }
    }
}