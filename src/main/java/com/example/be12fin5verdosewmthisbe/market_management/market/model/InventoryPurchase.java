package com.example.be12fin5verdosewmthisbe.market_management.market.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private Long buyerStoreId;

    private BigDecimal quantity;

    private int price;

    private purchaseStatus status;

    private purchaseMethod method;

    private Timestamp createdAt;

    public enum purchaseStatus {
        waiting,
        payment,
        delivery,
        end,
        cancelled
    }

    public enum purchaseMethod {
        credit_card,
        kakaopay,
        cash
    }
}
