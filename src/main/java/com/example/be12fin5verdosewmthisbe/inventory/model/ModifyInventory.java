package com.example.be12fin5verdosewmthisbe.inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "modifyinventory")
@Data
@NoArgsConstructor  // JPA에서 필요
@AllArgsConstructor // Builder 내부에서 사용
@Builder
@Schema(description = "수정한 재고")
public class ModifyInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "modifyinventoryid")
    @Schema(description = "수정한재고 ID", example = "1")
    private Long modifyinventoryId;

    @Column(name = "modify_date")
    @Schema(description = "수정한 날짜", example = "2025-04-01T10:00:00Z")
    private Timestamp modifyDate;

    @Column(name = "modify_quantity")
    @Schema(description = "수정한 수량", example = "+3")
    private String modifyQuantity;

    @OneToMany(mappedBy = "modifyInventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Schema(description = "수정된 재고 목록")
    private List<Inventory> inventoryList = new ArrayList<>();

}
