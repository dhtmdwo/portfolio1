package com.example.be12fin5verdosewmthisbe.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class WebSocketSender {

    private final WebSocketSessionManager sessionManager;

    public void sendToStore(String storeId, String message) {
        WebSocketSession session = sessionManager.getSession(storeId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace(); // 로깅 권장
            }
        }
    }
}
