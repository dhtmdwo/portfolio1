package com.example.be12fin5verdosewmthisbe.notification.controller;


import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventorySaleDto;
import com.example.be12fin5verdosewmthisbe.notification.service.NotificationService;
import com.example.be12fin5verdosewmthisbe.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/get")
    public BaseResponse<Boolean> getNoti(HttpServletRequest request) {
        Long storeId = getStoreId(request);
        Boolean result = notificationService.getNoti(storeId);
        return BaseResponse.success(result);
    }

    @PostMapping("/post")
    public BaseResponse<String> postNoti(HttpServletRequest request) {
        Long storeId = getStoreId(request);
        String result = notificationService.postNoti(storeId);
        return BaseResponse.success(result);
    }

    private Long getStoreId(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ATOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        Claims claims = jwtTokenProvider.getClaims(token);
        Long storeId = Long.valueOf(claims.get("storeId", String.class));
        return  storeId;
    }


}
