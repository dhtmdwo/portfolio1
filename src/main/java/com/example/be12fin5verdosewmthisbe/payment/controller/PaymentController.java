package com.example.be12fin5verdosewmthisbe.payment.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.payment.model.Payment;
import com.example.be12fin5verdosewmthisbe.payment.model.dto.PaymentCancelDto;
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
    public BaseResponse<String> verifyPayment(@RequestBody PaymentDto.PaymentVerifyRequest request) {

        // 주문정보테이블 생성 - status = pending

        PaymentDto.PaymentData paymentData = paymentService.savePayment(request.getImpUid(), request.getOrderId());

        //TODO inventoryPurchaseId로 구매할 재고의 희망가격 알아오기
        int amount = paymentData.getAmount();
        int DBamount = 1000; // 여기 바뀌어야함
        // 금액 등 검증
        if (!request.getMerchantUid().equals(paymentData.getMerchantUid()) && amount == DBamount) {
            // 주문정보테이블 수정 - status = cancelled
            Payment payment = paymentService.findById(paymentData.getPaymentId());
            payment.setStatus(Payment.PaymentStatus.FAILED);
            // 전액 결제 취소
            paymentService.cancelPayment(payment.getTransactionId(),"결제 정보 불일치",payment.getAmount());
            throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        }
        // 주문정보테이블 수정 - status = paid
        return BaseResponse.success("ok");
    }
    @PostMapping("/cancel")
    public BaseResponse<String> cancelPayment(@RequestBody PaymentCancelDto.RequestDto request) {

        paymentService.cancelPayment(request.getImpUid(), request.getReason(),request.getAmount());

        return BaseResponse.success("ok");
    }
}
        