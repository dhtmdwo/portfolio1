package com.example.be12fin5verdosewmthisbe.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer tableNumber;

    private Integer totalPrice;

    private OrderStatus status;

    private Timestamp createdAt;

    private OrderType orderType;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "주문에 들어온 메뉴 목록")
    private List<OrderMenu> OrderMenuList = new ArrayList<>();


    public enum OrderType {
        hall,
        coupang,
        baemin,
        yogiyo
    }

    public enum OrderStatus {
        CANCELLED,
        PAID
    }
}
        