package com.example.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient healthClient(WebClient.Builder builder) {
        // 기본 설정(타임아웃, 로깅 등) 추가 가능
        return builder
                .build();
    }
}

