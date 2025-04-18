package com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class MenuUpdateDto {

    @Data
    @Builder
    public static class RequestDto {
        private Long menuId;
        private String name;
        private Long categoryId;
        private int price;
        private List<MenuRegisterDto.MenuCreateRequestDto.IngredientDto> ingredients;

        @Data
        @Builder
        public static class IngredientDto {
            private Long storeInventoryId;
            private BigDecimal quantity;
        }
    }
}