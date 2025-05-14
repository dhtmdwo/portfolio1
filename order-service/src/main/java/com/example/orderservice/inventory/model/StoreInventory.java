package com.example.orderservice.inventory.model;

import com.example.orderservice.menu_management.menu.model.Recipe;
import com.example.orderservice.menu_management.option.model.OptionValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "store_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "매장별재고")
public class StoreInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "매장별재고 ID", example = "1")
    private Long id;

    @Schema(description = "입고날로부터 사용가능한 유통기한", example = "5")
    private Integer expiryDate;

    @Schema(description = "사용단위", example = "kg,g,ml")
    private String unit;

    @Schema(description = "총수량 (소수 가능)", example = "12.50(kg)")
    private BigDecimal quantity;

    @Schema(description = "이름", example = "마늘")
    private String name;

    @Schema(description = "최소수량", example = "5(kg)")
    private BigDecimal minQuantity;


    @OneToMany(mappedBy = "storeInventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Schema(description = "상세 재고 목록")
    private List<Inventory> inventoryList = new ArrayList<>();

    @OneToMany(mappedBy = "storeInventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "옵션 재고 사용량 목록")
    private List<OptionValue> optionValues = new ArrayList<>();

    @OneToMany(mappedBy = "storeInventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Schema(description = "레시피 목록")
    private List<Recipe> recipeList = new ArrayList<>();

    private Long storeId;

}

