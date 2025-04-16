package com.example.be12fin5verdosewmthisbe.menu_management.category.model.dto;

import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryDto {

    @Schema(description = "카테고리 등록 요청 DTO")
    @Getter
    public static class requestDto {
        @Schema(description = "카테고리 이름", example = "한식")
        private String name;
        private List<Long> optionIds;
    }
    @Schema(description = "카테고리 삭제 요청 DTO")
    @Getter
    public static class deleteDto {
        @Schema(description = "카테고리 아이디", example = "한식")
        private List<Long> ids;
    }



    @Schema(description = "카테고리 수정 요청 DTO")
    @Getter
    @Setter
    public static class updateDto {
        private Long id;
        private String newName;
        private List<Long> optionIds;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class responseDto {
        private Long id;
        private String name;
        private List<OptionDto> options;

        public static responseDto from(Category category) {
            return responseDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .options(category.getCategoryOptions().stream()
                            .map(co -> OptionDto.from(co.getOption()))
                            .collect(Collectors.toList()))
                    .build();
        }
    }
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class OptionDto {
        private Long id;
        private String name;

        public static OptionDto from(Option option) {
            return OptionDto.builder()
                    .id(option.getId())
                    .name(option.getName())
                    .build();
        }
    }

}