package com.example.inventoryservice.inventory.model.dto;

import lombok.Getter;

import java.util.List;

public class InventoryRecipes {
    @Getter
    public static class Request {
        private Long inventoryId;
    }
    @Getter
    public static class Response {
        private List<String> menuItems;

        private Response(List<String> menuItems) {
            this.menuItems = menuItems;
        }

        public static Response from(List<String> menuItems) {
            return new Response(menuItems);
        }
    }
}