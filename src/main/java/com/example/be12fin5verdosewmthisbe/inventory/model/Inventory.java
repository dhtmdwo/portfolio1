package com.example.be12fin5verdosewmthisbe.inventory.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "inventory")
@Data
@Schema(description = "재고")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    @Schema(description = "재고 ID", example = "1")
    private Long inventory_Id;

    @Column(name = "purchase_date")
    @Schema(description = "구매날짜", example = "2025-04-01T10:00:00Z")
    private Timestamp purchaseDate;

    @Column(name = "expiry_date")
    @Schema(description = "유통기한", example = "2026-04-01T00:00:00Z")
    private Timestamp expiryDate;

    @Column(name = "unit_price")
    @Schema(description = "단가", example = "1500")
    private Integer unitPrice;

    @Column(name = "quantity")
    @Schema(description = "수량 (소수 가능)", example = "12.50(kg)")
    private BigDecimal quantity;

}
