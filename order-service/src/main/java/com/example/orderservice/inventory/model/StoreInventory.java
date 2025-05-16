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

    @Schema(description = "이름", example = "마늘")
    private String name;

    private BigDecimal quantity;

    private String unit;

    @OneToMany(mappedBy = "storeInventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "옵션 재고 사용량 목록")
    private List<OptionValue> optionValues = new ArrayList<>();

    @OneToMany(mappedBy = "storeInventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Schema(description = "레시피 목록")
    private List<Recipe> recipeList = new ArrayList<>();

    private Long storeId;

}

