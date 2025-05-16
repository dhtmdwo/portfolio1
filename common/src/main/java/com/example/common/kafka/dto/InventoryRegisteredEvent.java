package com.example.common.kafka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "Inventory 등록 완료 이벤트")
public class InventoryRegisteredEvent {

    @Schema(description = "구매날짜", example = "2025-04-01T10:00:00Z")
    private Timestamp purchaseDate;

    @Schema(description = "연결된 매장별재고 ID (StoreInventory PK)", example = "1")
    private Long storeInventoryId;

    @Schema(description = "등록된 수량", example = "2.5")
    private BigDecimal quantity;

    @Schema(description = "단가", example = "3000")
    private Integer unitPrice;

    @Schema(description = "유통기한", example = "2026-04-01")
    private LocalDate expiryDate;
}
