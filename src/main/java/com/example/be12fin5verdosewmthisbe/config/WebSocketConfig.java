package com.example.be12fin5verdosewmthisbe.config;

import com.example.be12fin5verdosewmthisbe.notification.AuthHandshakeInterceptor;
import com.example.be12fin5verdosewmthisbe.notification.CustomWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CustomWebSocketHandler handler;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;

    public WebSocketConfig(CustomWebSocketHandler handler, AuthHandshakeInterceptor authHandshakeInterceptor) {
        this.handler = handler;
        this.authHandshakeInterceptor = authHandshakeInterceptor;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("http://localhost:5173");  // CORS 설정 필요 시 수정
    }
}
