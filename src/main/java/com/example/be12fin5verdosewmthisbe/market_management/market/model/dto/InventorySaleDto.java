package com.example.be12fin5verdosewmthisbe.market_management.market.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InventorySaleDto {

    @Data
    public static class InventorySaleRequestDto {
        private Long inventoryId;
        private BigDecimal quantity;
        private int price;
        private String content;
        private List<String> imageUrls;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventorySaleListDto {

        // 판매 물품 id
        private Long inventorySaleId;
        // 판매물품 이름
        private String inventoryName;
        // 수량
        private String quantity;
        // 유통기한
        private LocalDate expirationDate;
        // 희망 가격
        private int price;
        // 등록 날짜
        private LocalDate createdDate;
        // 파는 가게명
        private String sellerStoreName;
    }
}