package com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class MenuRegisterationDto {

    @Data
    @Builder
    public static class MenuCreateRequestDto {
        private String name;
        private Long categoryId;
        private int price;
        private List<IngredientDto> ingredients;

        @Data
        @Builder
        public static class IngredientDto {
            private Long storeInventoryId;
            private BigDecimal quantity;
        }
    }
}