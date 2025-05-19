package com.example.common.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreInventoryEvent {
    private Long id;             // storeInventory PK
    private String name;
    private BigDecimal quantity;
    private BigDecimal minQuantity;
    private int expiryDate;
    private String unit;
    private Long storeId;
}
