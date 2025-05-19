package com.example.common.kafka.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreInventoryCreateEvent {
    private Long storeId;
    private String unit;
    private String name;
    private BigDecimal minQuantity;
    private BigDecimal initialQuantity;
    private int expiryDate;



    private BigDecimal quantity;
    private int price;
}
