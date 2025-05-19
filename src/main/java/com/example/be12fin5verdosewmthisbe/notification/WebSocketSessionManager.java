package com.example.be12fin5verdosewmthisbe.notification;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;


@Component
public class WebSocketSessionManager {

    // storeId 기준으로 WebSocketSession을 저장
    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public void addSession(String storeId, WebSocketSession session) {
        sessionMap.put(storeId, session);
    }

    public void removeSession(String storeId) {
        sessionMap.remove(storeId);
    }

    public boolean hasSession(String storeId) {
        return sessionMap.containsKey(storeId);
    }

    public WebSocketSession getSession(String storeId) {
        return sessionMap.get(storeId);
    }
}