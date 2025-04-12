package com.example.be12fin5verdosewmthisbe.menu_management.category.model.dto;


import lombok.Getter;
import lombok.Setter;

public class CategoryDto {

    @Getter
    public static class requestDto {
        private String name;
    }
    @Getter
    @Setter
    public static class responseDto {
        private String name;
    }
}
        