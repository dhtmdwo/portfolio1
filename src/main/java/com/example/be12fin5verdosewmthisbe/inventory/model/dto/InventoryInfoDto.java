package com.example.be12fin5verdosewmthisbe.inventory.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

public class InventoryInfoDto {
    @Builder
    @Getter
    public static class Response{
        private String name;
        private BigDecimal quantity;
        private String unit;

        public static InventoryInfoDto.Response of(String name, BigDecimal quantity, String unit) {
            return InventoryInfoDto.Response.builder()
                    .name(name)
                    .quantity(quantity)
                    .unit(unit)
                    .build();
        }
    }
}
