package com.example.be12fin5verdosewmthisbe.menu_management.option.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class OptionDto {

    @Schema(description = "옵션 등록 요청 DTO")
    @Getter
    @Setter
    public static class RequestDto {
        @Schema(description = "옵션 이름", example = "라지 사이즈")
        private String name;

        @Schema(description = "옵션 가격", example = "1000")
        private int price;

        @Schema(description = "카테고리 ID", example = "3")
        private Long categoryId;

        @Schema(description = "재고별 사용 수량 리스트")
        private List<InventoryQuantityDto> inventoryQuantities;
    }

    @Schema(description = "재고 ID와 사용 수량 DTO")
    @Getter
    @Setter
    public static class InventoryQuantityDto {
        @Schema(description = "재고 ID", example = "20")
        private Long inventoryId;

        @Schema(description = "사용 수량", example = "1.5")
        private BigDecimal quantity;
    }

}
