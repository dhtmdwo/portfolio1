package com.example.be12fin5verdosewmthisbe.market_management.market.model.dto;

import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventoryPurchase;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InventoryPurchaseDto {
    @Data
    public static class InventoryPurchaseRequestDto {
        private Long inventorySaleId;     // 어떤 판매에 연결할지
        private Long buyerStoreId;
        private BigDecimal quantity;
        private int price;
        private String status;            // 예: "waiting", "payment"
        private String method;            // 예: "credit_card", "cash"
    }
    @Data
    public static class InventoryPurchaseResponseDto {
        private Long id;
        private String buyerStoreName;
        private BigDecimal quantity;
        private int price;
        private String status;
        private String method;
        private Timestamp createdAt;

        public InventoryPurchaseResponseDto(InventoryPurchase purchase, String buyerStoreName) {
            this.id = purchase.getId();
            this.buyerStoreName = buyerStoreName;
            this.quantity = purchase.getQuantity();
            this.price = purchase.getPrice();
            this.status = purchase.getStatus().name();
            this.method = purchase.getMethod().name();
            this.createdAt = purchase.getCreatedAt();
        }
    }
}
