package com.example.gatewayservice.filter;

import com.example.common.common.Jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("▶ JwtAuthGlobalFilter invoked! path={}", path);

        String token = extractTokenFromCookies(exchange);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.getClaims(token);
            if (claims != null) {
                String storeId = claims.get("storeId", String.class);
                String email   = claims.get("email",   String.class);
                ServerHttpRequest enriched = exchange.getRequest().mutate()
                        .header("X-Store-Id", storeId != null ? storeId : "")
                        .header("X-Email-Url", email != null ? email : "")
                        .build();
                exchange = exchange.mutate().request(enriched).build();
            }
        }

        if (path.startsWith("/api/user") || path.startsWith("/api/store") || path.startsWith("/api/email")) {
            return chain.filter(exchange);
        }

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return unauthorized(exchange);
        }

        Claims claims = jwtTokenProvider.getClaims(token);
        String storeId = claims.get("storeId", String.class);
        if (!StringUtils.hasText(storeId)) {
            return forbiddenNoStoreId(exchange);
        }

        return chain.filter(exchange);
    }

    // helper methods
    private Mono<Void> unauthorized(ServerWebExchange ex) {
        ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return ex.getResponse().setComplete();
    }

    private Mono<Void> forbiddenNoStoreId(ServerWebExchange ex) {
        ex.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        ex.getResponse().getHeaders().add("Error-Code", "NO_STORE_ID");
        return ex.getResponse().setComplete();
    }

    private String extractTokenFromCookies(ServerWebExchange exchange) {
        return exchange.getRequest().getCookies().values().stream()
                .flatMap(List::stream)
                .filter(c -> "ATOKEN".equals(c.getName()))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElse(null);
    }


    @Override
    public int getOrder() {
        return -100; // 인증 로직이 가능한 빨리 실행되도록 우선순위 설정
    }
}
