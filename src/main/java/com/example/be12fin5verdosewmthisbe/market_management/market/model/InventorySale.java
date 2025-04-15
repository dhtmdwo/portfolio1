package com.example.be12fin5verdosewmthisbe.market_management.market.model;

import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Recipe;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.border.EmptyBorder;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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


    @OneToMany(mappedBy = "inventorySale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "구매 요청 목록")
    private List<InventoryPurchase> purchaseList = new ArrayList<>();

    public enum saleStatus {
        available,
        waiting,
        delivery,
        sold,
        cancelled
    }
}
        