package com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class MenuDto {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuListResponseDto {
        private String name;
        private String category;
        private String ingredients;

    }

}
