package com.example.be12fin5verdosewmthisbe.menu_management.option.model.dto;


import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.OptionValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class OptionDto {

    @Schema(description = "옵션 등록 요청 DTO")
    @Getter
    @Setter
    public static class RequestDto {
        @Schema(description = "옵션 이름", example = "라지 사이즈")
        private String name;

        @Schema(description = "옵션 가격", example = "1000")
        private int price;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseDto {
        private Long optionId;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailResponseDto {
        private Long optionId;
        private String name;
        private int price;
        private List<OptionValueDto> optionValues;

        public static DetailResponseDto from(Option option) {
            List<OptionValueDto> values = option.getOptionValueList().stream()
                    .map(OptionValueDto::from)
                    .collect(Collectors.toList());

            return new DetailResponseDto(
                    option.getId(),
                    option.getName(),
                    option.getPrice(),
                    values
            );
        }
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionValueDto {
        private Long inventoryId;
        private int quantity;

        public static OptionValueDto from(OptionValue ov) {
            return new OptionValueDto(
                    ov.getInventoryId(),
                    ov.getQuantity().toBigInteger().bitCount()
            );
        }
    }

}
