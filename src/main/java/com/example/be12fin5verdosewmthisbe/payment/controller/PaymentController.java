package com.example.be12fin5verdosewmthisbe.payment.controller;

import com.example.be12fin5verdosewmthisbe.payment.model.dto.PaymentDto;
import com.example.be12fin5verdosewmthisbe.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentDto.PaymentVerifyRequest request) {

        // 주문정보테이블 생성 - status = pending

        PaymentDto.PaymentData paymentData = paymentService.savePayment(request.getImpUid(), request.getOrderId());

        //TODO inventoryPurchaseId로 구매할 재고의 희망가격 알아오기
        int amount = paymentData.getAmount();
        int DBamount = 1000; // 여기 바뀌어야함
        // 금액 등 검증
        if (!request.getMerchantUid().equals(paymentData.getMerchantUid()) && amount == DBamount) {
            // 주문정보테이블 수정 - status = cancelled

            return ResponseEntity.badRequest().body("결제 정보 불일치");
        }

        // 성공 처리 (DB 저장 등)
        // 주문정보테이블 수정 - status = paid
        return ResponseEntity.ok("success");
    }
}
        