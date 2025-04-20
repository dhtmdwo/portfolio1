package com.example.be12fin5verdosewmthisbe.menu_management.menu.model;

import com.example.be12fin5verdosewmthisbe.inventory.model.Inventory;
import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "레시피 정보")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "레시피 ID (자동 생성)", example = "1")
    private Long id;

    private String inventoryId;

    @Schema(description = "재고 ID", example = "inventory-123")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_inventory_id")
    private StoreInventory storeInventory;

    @Schema(description = "레시피 가격", example = "2500.00")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    @Schema(description = "레시피가 속한 메뉴 정보")
    private Menu menu;
}