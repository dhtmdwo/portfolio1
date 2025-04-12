package com.example.be12fin5verdosewmthisbe.menu_management.option.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class OptionUpdateDto {

    @Schema(description = "옵션 수정 요청 DTO")
    @Getter
    @Setter
    public static class RequestDto {
        @Schema(description = "수정할 옵션 ID", required = true, example = "1")
        private Long optionId;

        @Schema(description = "새로운 옵션 이름", example = "미디움 사이즈")
        private String name;

        @Schema(description = "새로운 옵션 가격", example = "700")
        private Integer price;

        @Schema(description = "새로운 카테고리 ID", example = "3")
        private Long categoryId;

        @Schema(description = "재고별 사용 수량 업데이트 또는 추가 리스트")
        private List<InventoryQuantityUpdateDto> inventoryQuantities;
    }

    @Schema(description = "재고 ID와 사용 수량 업데이트 DTO")
    @Getter
    @Setter
    public static class InventoryQuantityUpdateDto {
        @Schema(description = "재고 ID", required = true, example = "20")
        private Long inventoryId;

        @Schema(description = "새로운 사용 수량", example = "1.8")
        private BigDecimal quantity;
    }
}