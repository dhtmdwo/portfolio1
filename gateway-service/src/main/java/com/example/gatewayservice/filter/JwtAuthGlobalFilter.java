package com.example.gatewayservice.filter;

import com.example.common.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("▶ JwtAuthGlobalFilter invoked! path={}", path);

        if (path.startsWith("/api/user")) {
            return chain.filter(exchange);
        }

        String token = null;

        MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
        for (List<HttpCookie> cookieList : cookies.values()) {
            for (HttpCookie cookie : cookieList) {
                if ("ATOKEN".equals(cookie.getName())) {
                    log.info("[ATOKEN Cookie] value={}", cookie.getValue());
                    token = cookie.getValue();
                    break; // inner for문 탈출
                }
            }
            if (token != null) {
                break; // outer for문 탈출
            }
        }

        if (token != null) {
            log.info("token={}", token);
            try {
                if(jwtTokenProvider.validateToken(token)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                // 2) 토큰 파싱
                Claims claims = jwtTokenProvider.getClaims(token);
                String storeId = claims.get("storeId", String.class);
                String emailUrl = claims.get("emailUrl", String.class);  // 추가된 부분
                log.info(storeId + ":" + emailUrl);

                if (storeId == null || storeId.isEmpty()) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 또는 HttpStatus.FORBIDDEN, 또는 HttpStatus.valueOf(419)
                    // 필요하다면 바디나 헤더에 상세 메시지 추가
                    exchange.getResponse().getHeaders().add("Error-Code", "NO_STORE_ID");
                    return exchange.getResponse().setComplete();
                }


                // 3) downstream 요청에 X-Store-Id 헤더로 전달
                ServerHttpRequest mutatedReq = exchange.getRequest().mutate()
                        .header("X-Store-Id", storeId != null ? storeId : "")
                        .header("X-Email-Url", emailUrl != null ? emailUrl : "")
                        .build();
                exchange = exchange.mutate().request(mutatedReq).build();
            } catch (JwtException e) {
                // 토큰 검증 실패 시 401 바로 리턴
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100; // 인증 로직이 가능한 빨리 실행되도록 우선순위 설정
    }
}
