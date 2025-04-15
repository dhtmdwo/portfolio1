package com.example.be12fin5verdosewmthisbe.inventory.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "inventory_purchase")
@Data
@Schema(description = "구매할 재고 내역")
public class Inventory_Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Purchase_id")
    @Schema(description = "구매재고 ID", example = "1")
    private Long Purchase_Id;

    @Column(name = "price")
    @Schema(description = "희망가격", example = "12000")
    private Integer price;

    @Column(name = "quantity")
    @Schema(description = "수량", example = "12000")
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(description = "구매 상태", example = "AVAILABLE", allowableValues = {"WAITING", "PAYMENT", "DELIVERY","END","CANCELED"})
    private Status status;

    @Column(name = "create_at")
    @Schema(description = "등록날짜", example = "12000")
    private Timestamp Create_at;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    @Schema(description = "구매 상태", example = "AVAILABLE", allowableValues = {"CREDIT_CARD", "BANK_TRANSFER", "KAKAOPAY"})
    private Method method;
}
public enum Status {
    WAITING, PAYMENT, DELIVERY,END,CANCELED
}

public enum Method {
    CREDIT_CARD, BANK_TRANSFER, KAKAOPAY
}