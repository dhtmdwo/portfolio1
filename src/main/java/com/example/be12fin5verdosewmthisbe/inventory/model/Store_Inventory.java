package com.example.be12fin5verdosewmthisbe.inventory.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "store_inventory")
@Data
@Schema(description = "매장별재고")
public class Store_Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Store_inventory_id")
    @Schema(description = "매장별재고 ID", example = "1")
    private Long Store_inventory_Id;

    @Column(name = "expiry_date")
    @Schema(description = "유통기한", example = "2026-04-01T00:00:00Z")
    private Timestamp expiryDate;

    @Column(name = "unit")
    @Schema(description = "사용단위", example = "kg,g,ml")
    private String unit;

    @Column(name = "quantity")
    @Schema(description = "총수량 (소수 가능)", example = "12.50(kg)")
    private BigDecimal quantity;

    @Column(name = "name")
    @Schema(description = "이름", example = "마늘")
    private String name;

    @Column(name = "minimum_quantity")
    @Schema(description = "최소수량", example = "12.50(kg)")
    private BigDecimal minimum_quantity;

}

