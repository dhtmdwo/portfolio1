package com.example.be12fin5verdosewmthisbe.market_management.market.model.dto;

import lombok.Data;

import java.math.BigDecimal;

public class InventoryPurchaseDto {
    @Data
    public class InventoryPurchaseRequestDto {
        private Long inventorySaleId;     // 어떤 판매에 연결할지
        private Long buyerStoreId;
        private BigDecimal quantity;
        private int price;
        private String status;            // 예: "waiting", "payment"
        private String method;            // 예: "credit_card", "cash"
    }
}
