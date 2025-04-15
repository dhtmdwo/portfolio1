package com.example.be12fin5verdosewmthisbe.inventory.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "inventory_sale")
@Data
@Schema(description = "판매할재고 내역")
public class Inventory_sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Sale_id")
    @Schema(description = "판매재고 ID", example = "1")
    private Long Sale_Id;

    @Column(name = "price")
    @Schema(description = "희망가격", example = "12000")
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "sales_status", nullable = false)
    @Schema(description = "판매 상태", example = "AVAILABLE", allowableValues = {"AVAILABLE", "OUT_OF_STOCK", "DISCONTINUED"})
    private SalesStatus salesStatus;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "소개내용", example = "마늘 남아서 팝니다")
    private String content;

    @Column(name = "name")
    @Schema(description = "이름", example = "마늘")
    private String name;

    @Column(name = "created_at")
    @Schema(description = "등록날짜", example = "2026-04-01T00:00:00Z")
    private Timestamp created_at;

    @Column(name = "image")
    @Schema(description = "판매할 재고의 이미지", example = "C:/images/upload")
    private String url;


}
public enum SalesStatus {
    SOLD, AVAILABLE, OUT_OF_STOCK;
}


