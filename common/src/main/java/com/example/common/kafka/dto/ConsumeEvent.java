package com.example.common.kafka.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumeEvent {
    private Long storeInventoryId;
    private BigDecimal quantity;
}
