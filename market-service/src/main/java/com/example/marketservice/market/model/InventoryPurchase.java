package com.example.marketservice.market.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String inventoryName;

    private BigDecimal quantity;

    private int price;

    private String unit;

    private purchaseStatus status;

    private purchaseMethod method;

    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private StoreInventory storeInventory;

    @ManyToOne(fetch = FetchType.LAZY)
    private InventorySale inventorySale;

    // 구매한 매장
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    public enum purchaseStatus {
        PENDING_APPROVAL,
        isPaymentInProgress,
        confirmDelivery,
        end,
        cancelled
    }

    public enum purchaseMethod {
        credit_card,
        kakaopay,
        cash
    }
}