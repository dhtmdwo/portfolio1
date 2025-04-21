package com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;

public class MenuSaleDto {
    @Getter
    public static class DateRequest{
        LocalDate startDate;
        LocalDate endDate;
    }

    @Builder
    @Getter
    public static class Response{
        private Timestamp date;
        private String category;
        private String menuName;
        private int quantity;

        public static MenuSaleDto.Response of(Timestamp date, String category, String menuName, int quantity) {
            return MenuSaleDto.Response.builder()
                    .date(date)
                    .menuName(menuName)
                    .category(category)
                    .quantity(quantity)
                    .build();
        }
    }
}
