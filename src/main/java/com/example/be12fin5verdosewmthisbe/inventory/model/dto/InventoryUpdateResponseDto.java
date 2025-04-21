package com.example.be12fin5verdosewmthisbe.inventory.model.dto;


import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryUpdateResponseDto {


    @JsonProperty("store_inventory_id")
    private Long storeInventoryid;
    private String name;
    private Integer miniquantity;
    private String unit;
    private Integer expiryDate;

    public static InventoryUpdateResponseDto from(StoreInventory inventory) {
        return InventoryUpdateResponseDto.builder()
                .storeInventoryid(inventory.getStoreinventoryId())
                .name(inventory.getName())
                .miniquantity(inventory.getMiniquantity())
                .unit(inventory.getUnit())
                .expiryDate(inventory.getExpiryDate())
                .build();
    }
}
