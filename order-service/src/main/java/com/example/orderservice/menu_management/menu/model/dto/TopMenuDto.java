package com.example.orderservice.menu_management.menu.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopMenuDto {
    private Long storeId;
    private Long menuId;
    private Long totalCount;
}
