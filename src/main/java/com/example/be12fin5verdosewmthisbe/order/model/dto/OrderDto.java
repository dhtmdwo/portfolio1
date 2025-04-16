package com.example.be12fin5verdosewmthisbe.order.model.dto;


import com.example.be12fin5verdosewmthisbe.order.model.Order;
import lombok.Data;

import java.util.List;

public class OrderDto {
    @Data
    public static class OrderCreateRequest {
        private Integer tableNumber;
        private String orderType;
        private List<OrderMenuRequest> orderMenus;
    }

    @Data
    public static class OrderMenuRequest {
        private Long menuId;
        private Integer quantity;
        private Integer price;
        private List<Long> optionIds;
    }
}