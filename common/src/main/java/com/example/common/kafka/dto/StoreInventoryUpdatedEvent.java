package com.example.common.kafka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "StoreInventory 업데이트 이벤트")
public class StoreInventoryUpdatedEvent {
    @Schema(description = "재고 ID", example = "1")
    private Long id;

    @Schema(description = "남은 총 수량", example = "12.5")
    private BigDecimal remainingQuantity;
}
