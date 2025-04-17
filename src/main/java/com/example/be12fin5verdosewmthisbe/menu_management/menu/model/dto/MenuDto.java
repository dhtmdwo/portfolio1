package com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto;

import lombok.Data;

import java.util.List;

public class MenuDto {
    @Data
    public static class MenuListResponseDto {
        private String name;
        private String category;
        private List<ingridientDto> ingridients;
    }

    @Data
    public static class ingridientDto {
        private String name;
        private String quantity;
        private String unit;
    }
}
