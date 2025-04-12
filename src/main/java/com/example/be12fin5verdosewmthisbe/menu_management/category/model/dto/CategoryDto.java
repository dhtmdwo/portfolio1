package com.example.be12fin5verdosewmthisbe.menu_management.category.model.dto;


import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CategoryDto {

    @Getter
    public static class requestDto {
        private String name;
    }

    @Getter
    public static class updateDto {
        private String oldName;
        private String newName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class responseDto {
        private String name;

        public static responseDto from(Category category) {
            return new responseDto(category.getName());
        }
    }
}
        