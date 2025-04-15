package com.example.be12fin5verdosewmthisbe.inventory.model.dto;


import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Builder
public class InventoryDetailRequestDto {

    private Integer inventoryId;
    @Schema(description = "재고명", required = true, example = "name")
    private String name;

    @Schema(description = "유통기한", required = true, example = "2")
    private Integer expiryDate;

    @Schema(description = "최소수량", required = true, example = "2")
    private Integer miniquantity;

    @Schema(description = "용량/단위", required = true, example = "12kg")
    private String unit;

    public StoreInventory toEntity() {
        return StoreInventory.builder()
                .name(this.name)
                .expiryDate(this.expiryDate)
                .miniquantity(this.miniquantity)
                .unit(this.unit)
                .build();
    }
}
