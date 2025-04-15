package com.example.be12fin5verdosewmthisbe.market_management.market.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class InventorySaleDto {

    @Data
    public class InventorySaleRequestDto {
        private Long inventoryId;
        private Long sellerStoreId;
        private BigDecimal quantity;
        private int price;
        private String status; // "available", "waiting", ...
        private String content;
        private List<String> imageUrls;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventorySaleResponseDto {
        private String inventoryName;
        private String sellerStoreName;
        private BigDecimal quantity;
        private int price;
    }
}
        