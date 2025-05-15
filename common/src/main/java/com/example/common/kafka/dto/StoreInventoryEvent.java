package com.example.common.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreInventoryEvent {
    private Long id;             // storeInventory PK
    private String name;
    private String unit;
    private Long storeId;
}
