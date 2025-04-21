package com.example.be12fin5verdosewmthisbe.order.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

public class OrderMonthDto {

    @Builder
    @Getter
    public static class TotalRequest{
        private int month;
        private int year;
    }


    @Builder
    @Getter
    public static class TotalSaleResponse{
        private Date date;
        private Integer sales;
        private Integer number; // 건수

        public static TotalSaleResponse of(Date date, Integer sales, Integer number) {
            return TotalSaleResponse.builder()
                    .date(date)
                    .sales(sales)
                    .number(number)
                    .build();
        }
    }

}
