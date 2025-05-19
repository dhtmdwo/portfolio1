package com.example.marketservice.market.model.dto;

import lombok.*;

import java.util.List;


public class StoreDto {


    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class response {

        private String name;
        private String address;
        private String phoneNumber;
        private Double latitude;
        private Double longitude;
        private List<InventorySaleDto.InventorySaleListDto> boardList;
    }
}
        