package com.example.inventoryservice.inventory.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "top_modified_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopModifiedInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate recordDate;

    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_inventory_id")
    private StoreInventory storeInventory;

    private BigDecimal modifyQuantity;

    private BigDecimal modifyRate;
}
