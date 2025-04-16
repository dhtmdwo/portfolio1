package com.example.be12fin5verdosewmthisbe.store.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.security.JwtTokenProvider;
import com.example.be12fin5verdosewmthisbe.store.model.dto.StoreDto;
import com.example.be12fin5verdosewmthisbe.store.service.StoreService;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserDto;
import com.example.be12fin5verdosewmthisbe.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {
    // Your code here
    private final StoreService storeService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public BaseResponse<String> registerStore(HttpServletRequest request, HttpServletResponse response, @RequestBody StoreDto.RegistRequest dto) {

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
        // JWT 읽기

        String emailUrl = claims.get("email", String.class);
        String storeId = storeService.registerStore(dto, emailUrl);
        String jwtToken = jwtTokenProvider.createToken(emailUrl, storeId);

        ResponseCookie cookie = ResponseCookie
                .from("ATOKEN", jwtToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofHours(1L))
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());



        return BaseResponse.success("가게등록에 성공했습니다.");
    }

}
        