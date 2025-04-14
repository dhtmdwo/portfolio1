package com.example.be12fin5verdosewmthisbe.payment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class PaymentCancelDto {
    @Data
    @AllArgsConstructor
    public static class RequestDto {
        private String impUid;
        private Integer amount;
        private String reason;
    }
    @Data
    public class ResponseDto {
        private String impUid;
        private String merchantUid;
        private Long orderId;
        private Long inventoryPurchaseId;
    }
}
