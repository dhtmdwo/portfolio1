package com.example.be12fin5verdosewmthisbe.menu_management.option.model.dto;


import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.OptionValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class OptionDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryQuantityDto {
        private Long inventoryId;
        private BigDecimal quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequestDto {
        private String name;
        private int price;
        private List<InventoryQuantityDto> inventoryQuantities;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseDto {
        private Long optionId;
        private String name;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class IngredientDto {
        private Long storeInventoryId;
        private String name;
        private BigDecimal quantity;
        private String unit;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DetailResponseDto {
        private Long id;
        private String name;
        private int price;
        private List<IngredientDto> ingredients;

        public static DetailResponseDto from(Option option) {
            return DetailResponseDto.builder()
                    .id(option.getId())
                    .name(option.getName())
                    .price(option.getPrice())
                    .ingredients(option.getOptionValueList().stream()
                            .map(ov -> IngredientDto.builder()
                                    .storeInventoryId(ov.getStoreInventory().getStoreinventoryId())
                                    .name(ov.getStoreInventory().getName())
                                    .quantity(ov.getQuantity())
                                    .unit(ov.getStoreInventory().getUnit())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        }
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequestDto {
        private Long optionId;
        private String name;
        private int price;
        private List<InventoryQuantityDto> inventoryQuantities;
    }

}
