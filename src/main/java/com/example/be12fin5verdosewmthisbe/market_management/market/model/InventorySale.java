package com.example.be12fin5verdosewmthisbe.market_management.market.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.border.EmptyBorder;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySale {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long inventoryId;

    private Long sellerStoreId;

    private BigDecimal quantity;

    private int price;

    private saleStatus status;

    private String content;

    private Timestamp createdAt;

    public enum saleStatus {
        available,
        waiting,
        delivery,
        sold,
        cancelled
    }
}
        