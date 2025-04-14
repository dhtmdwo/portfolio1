package com.example.be12fin5verdosewmthisbe.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "orders")
@Data
@Schema(description = "주문 정보")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "주문 ID", example = "1")
    private Integer id;

    @Column(name = "purchase_id", nullable = false, insertable = false, updatable = false)
    @Schema(description = "구매재고내역 ID", example = "123")
    private Integer purchaseId;

    @Column(name = "total_amount", nullable = false)
    @Schema(description = "총 주문 금액", example = "20000")
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(description = "주문 상태", example = "PENDING", allowableValues = {"PENDING", "PAID", "CANCELLED"})
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "주문 생성 시간", example = "2023-10-27T10:00:00Z")
    private Timestamp createdAt;

    public enum OrderStatus {
        PENDING,
        PAID,
        CANCELLED
    }
}