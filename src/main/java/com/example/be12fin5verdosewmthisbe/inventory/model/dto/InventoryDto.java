package com.example.be12fin5verdosewmthisbe.inventory.model.dto;


import com.example.be12fin5verdosewmthisbe.inventory.model.Inventory;
import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDto {

    private Long storeInventoryId;
    private Integer totalPrice;
    private Timestamp purchaseDate;
    private BigDecimal quantity;

    @Data
    public static class InventoryRegisterDto {
        // 입고할 재고
        private Long storeInventoryId;
        // 입고할 재고의 수량
        private BigDecimal quantity;
        // 입고할 재고의 가격
        private Integer price;

    }

}
        