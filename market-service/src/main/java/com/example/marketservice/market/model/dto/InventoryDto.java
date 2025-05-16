package com.example.marketservice.market.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryRegisterDto {
        // 입고할 재고
        @NotNull(message = "storeInventoryId는 필수입니다.")
        private Long storeInventoryId;
        // 입고할 재고의 수량
        @NotNull(message = "수량은 필수입니다.")
        @DecimalMin(value = "0.1", inclusive = true, message = "수량은 0.1 이상이어야 합니다.")
        @Schema(description = "수량", required = true, example = "2")
        private BigDecimal quantity;
        // 입고할 재고의 가격
        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        private Integer price;

    }

}
        