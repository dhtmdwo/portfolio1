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
        String accessToken = paymentService.getAccessToken();
        PaymentDto.PaymentData paymentData = paymentService.getPaymentData(request.getImpUid(), accessToken);

        // 금액 등 검증
        if (!request.getMerchantUid().equals(paymentData.getMerchantUid())) {
            return ResponseEntity.badRequest().body("결제 정보 불일치");
        }

        // 성공 처리 (DB 저장 등)
        return ResponseEntity.ok("success");
    }
}
        