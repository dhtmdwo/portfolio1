package com.example.orderservice.menu_management.menu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "top_selling_menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopSellingMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;

    @ManyToOne
    private Menu menu;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Column(name = "total_count")
    private Long totalCount;
}

