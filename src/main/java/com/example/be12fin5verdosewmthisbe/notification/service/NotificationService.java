package com.example.be12fin5verdosewmthisbe.notification.service;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.inventory.service.InventoryService;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventoryPurchaseDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.InventorySaleRepository;
import com.example.be12fin5verdosewmthisbe.notification.WebSocketSender;
import com.example.be12fin5verdosewmthisbe.notification.WebSocketSessionManager;
import com.example.be12fin5verdosewmthisbe.notification.model.PurchaseNotification;
import com.example.be12fin5verdosewmthisbe.notification.repository.PurchaseNotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final WebSocketSessionManager sessionManager;
    private final WebSocketSender webSocketSender;
    private final InventorySaleRepository inventorySaleRepository;
    private final PurchaseNotificationRepository purchaseNotificationRepository;

    public void sendNotification(InventoryPurchaseDto.InventoryPurchaseRequestDto dto) {
        // 1. DB에 저장

        // 2. 실시간 전송 or Redis 저장
        InventorySale sale = inventorySaleRepository.findById(dto.getInventorySaleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SALE_NOT_FOUND));
        Long storeId = sale.getStore().getId();
        String receiverId = storeId.toString();
        String message = "구매 요청이 왔습니다.";

        Optional<PurchaseNotification> optional = purchaseNotificationRepository.findBySellerStoreId(storeId);


        if(optional.isPresent()) {
            PurchaseNotification notification = optional.get();
            notification.setRead(false); // 또는 notification.setIsRead(false);
            purchaseNotificationRepository.save(notification);
        }
        else{
            PurchaseNotification notification = PurchaseNotification.builder()
                    .sellerStoreId(storeId)
                    .isRead(false)
                    .build();
            purchaseNotificationRepository.save(notification);
        }

        if (sessionManager.hasSession(receiverId)) {
            webSocketSender.sendToStore(receiverId, message);
        }
    }

    public void deliverPendingNotifications(String receiverId) {
        List<Object> pending = redisTemplate.opsForList().range("alarm:" + receiverId, 0, -1);
        if (pending != null && !pending.isEmpty()) {
            for (Object msg : pending) {
                webSocketSender.sendToStore(receiverId, msg.toString());
            }
            redisTemplate.delete("alarm:" + receiverId);
        }
    }

    public Boolean getNoti(Long storeId) {
        Optional<PurchaseNotification> optional = purchaseNotificationRepository.findBySellerStoreId(storeId);

        if(optional.isPresent()) {
            PurchaseNotification notification = optional.get();
            if(notification.isRead() == false) {
                return false;
            }
            else{
                return true;
            }
        }
        return true;
    }

    public String postNoti(Long storeId) {
        Optional<PurchaseNotification> optional = purchaseNotificationRepository.findBySellerStoreId(storeId);

        if(optional.isPresent()) {
            PurchaseNotification notification = optional.get();
            notification.setRead(true);  //true가 읽은거
            purchaseNotificationRepository.save(notification);
        }
        return "ok";
    }


}