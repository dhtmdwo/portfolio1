package com.example.inventoryservice.inventory.model.dto;

import lombok.Builder;
import lombok.Getter;

public class InventoryCallDto {
    @Builder
    @Getter
    public static class Response {
        private int expiringCount;
        private int reorderRequiredCount;
        private int receivedTodayCount;


        public static Response of(int expiringCount, int reorderRequiredCount, int receivedTodayCount) {
            return Response.builder()
                    .expiringCount(expiringCount)
                    .reorderRequiredCount(reorderRequiredCount)
                    .receivedTodayCount(receivedTodayCount)
                    .build();
        }
    }
}
