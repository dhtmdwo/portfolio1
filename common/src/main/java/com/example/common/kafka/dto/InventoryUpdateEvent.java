package com.example.common.kafka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateEvent {
    @Schema(description = "재고 ID", example = "1")
    private Long id;

    private LocalDate expiryDate;

    private BigDecimal quantity;
}
