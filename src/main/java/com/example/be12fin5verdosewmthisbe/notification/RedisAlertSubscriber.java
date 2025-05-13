package com.example.be12fin5verdosewmthisbe.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RedisAlertSubscriber implements MessageListener {

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private WebSocketSender sender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        AlertPayload payload = deserialize(message);

        if (sessionManager.hasSession(payload.getStoreId())) {
            sender.sendToStore(payload.getStoreId(), payload.getMessage());
        }
    }

    private AlertPayload deserialize(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, AlertPayload.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
