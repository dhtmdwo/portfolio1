package com.example.be12fin5verdosewmthisbe.notification;

import com.example.be12fin5verdosewmthisbe.notification.WebSocketSessionManager;
import com.example.be12fin5verdosewmthisbe.notification.service.NotificationService;
import com.example.be12fin5verdosewmthisbe.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final WebSocketSessionManager sessionManager;
    private final NotificationService notificationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        String token = extractTokenFromQuery(session);
//        if (token == null || !jwtTokenProvider.validateToken(token)) {
//            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid or missing token"));
//            return;
//        }
        //String storeId = jwtTokenProvider.getStoreIdFromToken(token);

        String storeId = (String) session.getAttributes().get("storeId");
        if (storeId != null) {
            sessionManager.addSession(storeId, session);
            System.out.println("✅ WebSocket 연결됨: storeId = " + storeId);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("storeId not found in token"));
        }
        notificationService.deliverPendingNotifications(storeId); // 알람 전달
        
        
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String token = extractTokenFromQuery(session);
        String storeId = jwtTokenProvider.getStoreIdFromToken(token);
        if (storeId != null) {
            sessionManager.removeSession(storeId);
            System.out.println("❌ WebSocket 연결 해제됨: 사용자 " + storeId);
        }
    }

    private String extractTokenFromQuery(WebSocketSession session) {
        if (session.getUri() == null || session.getUri().getQuery() == null) return null;
        for (String param : session.getUri().getQuery().split("&")) {
            if (param.startsWith("token=")) {
                return param.substring("token=".length());
            }
        }
        return null;
    }
}