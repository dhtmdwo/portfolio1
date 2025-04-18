package com.example.be12fin5verdosewmthisbe.order.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class OrderSaleDetailDto {

    @Getter
    public static class OrderSaleDetailRequest{
        LocalDate startDate;
        LocalDate endDate;
    }

    @Getter
    @Builder
    public static class OrderSaleDetailResponse{
        String monthDateTime;
        EachSaleDetailResponse hall;
        EachSaleDetailResponse baemin;
        EachSaleDetailResponse coupang;
        EachSaleDetailResponse yogiyo;

        public static OrderSaleDetailResponse of(String monthDateTime, EachSaleDetailResponse hall, EachSaleDetailResponse baemin, EachSaleDetailResponse coupang, EachSaleDetailResponse yogiyo){

            return OrderSaleDetailResponse.builder()
                    .monthDateTime(monthDateTime)
                    .hall(hall)
                    .baemin(baemin)
                    .coupang(coupang)
                    .yogiyo(yogiyo)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EachSaleDetailResponse{
        String saleMethod;
        int saleQuantity;
        int salePrice;

        public static EachSaleDetailResponse of(String saleMethod, int saleQuantity, int salePrice){
            return EachSaleDetailResponse.builder()
                    .saleMethod(saleMethod)
                    .saleQuantity(saleQuantity)
                    .salePrice(salePrice)
                    .build();
        }

    }


}
