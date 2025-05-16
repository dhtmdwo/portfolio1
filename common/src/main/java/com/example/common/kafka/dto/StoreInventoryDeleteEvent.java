package com.example.common.kafka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "매장 재고 삭제 이벤트")
public class StoreInventoryDeleteEvent {
    @Schema(description = "삭제할 재고 ID 리스트", example = "[1,2,3]")
    private List<Long> inventoryIds;
}
