// 1. 공통 모듈에 DTO 정의
package com.example.common.kafka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Schema(description = "재고 등록/추가 이벤트")
public class InventoryRegisterEvent {
    @Schema(description = "매장별재고 ID", example = "1")
    private Long storeInventoryId;

    @Schema(description = "추가 수량", example = "12.5")
    private BigDecimal quantity;

    @Schema(description = "단가", example = "3000")
    private int price;

}
