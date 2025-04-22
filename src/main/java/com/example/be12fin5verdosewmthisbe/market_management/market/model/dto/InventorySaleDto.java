package com.example.be12fin5verdosewmthisbe.market_management.market.model.dto;


import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InventorySaleDto {

    @Data
    public static class InventorySaleRequestDto {
        private Long storeInventoryId;
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

    @Data
    @Builder
    @AllArgsConstructor
    public static class InventorySaleDetailDto {
        private Long id;
        private String inventoryName;
        private String sellerStoreName;
        private String buyerStoreName;
        private BigDecimal quantity;
        private int price;
        private String status;
        private String content;
        private LocalDate expiryDate;
        private LocalDate createdDate;
        private List<String> imageUrls;

        public static InventorySaleDetailDto fromEntity(InventorySale entity) {
            return InventorySaleDetailDto.builder()
                    .id(entity.getId())
                    .inventoryName(entity.getInventoryName())
                    .sellerStoreName(entity.getSellerStoreName())
                    .buyerStoreName(entity.getBuyerStoreName())
                    .quantity(entity.getQuantity())
                    .price(entity.getPrice())
                    .status(entity.getStatus().toString())
                    .content(entity.getContent())
                    .expiryDate(entity.getExpiryDate())
                    .createdDate(entity.getCreatedAt().toLocalDateTime().toLocalDate())
                    .imageUrls(entity.getImageList().stream()
                            .map(image -> image.getUrl()) // 이미지 객체에 따라 수정
                            .toList())
                    .build();
        }
    }


}