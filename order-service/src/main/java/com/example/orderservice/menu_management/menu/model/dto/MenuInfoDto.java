package com.example.orderservice.menu_management.menu.model.dto;

import lombok.Builder;
import lombok.Getter;

public class MenuInfoDto {
    @Builder
    @Getter
    public static class MenuResponse{
        private String menuName;
        private String category;

        public static MenuResponse of(String menuName, String category) {
            return MenuResponse.builder()
                    .menuName(menuName)
                    .category(category)
                    .build();
        }
    }
}
