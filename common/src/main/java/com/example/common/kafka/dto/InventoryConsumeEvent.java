package com.example.common.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryConsumeEvent {
    private Long storeId;
    private Map<Long, BigDecimal> usedInventoryQty;
    private String occurredAt;
}