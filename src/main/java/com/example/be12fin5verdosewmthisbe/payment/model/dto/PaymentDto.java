package com.example.be12fin5verdosewmthisbe.payment.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class PaymentDto {
    @Data
    @AllArgsConstructor
    public static class PaymentData {
        private String merchantUid;
        private int amount;
    }
    @Data
    public class PaymentVerifyRequest {
        private String impUid;
        private String merchantUid;
        private int inventoryPurchaseId;
    }

}
        