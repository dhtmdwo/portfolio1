package com.example.common.kafka.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreInventoryCreateEvent {
    private Long storeId;
    private String unit;
    private String name;
    private BigDecimal minQuantity;
    private BigDecimal initialQuantity;
    private int expiryDate;
}
