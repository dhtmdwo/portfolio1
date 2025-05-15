package com.example.userservice.store.controller;

import com.example.common.common.BaseResponse;
import com.example.common.common.JwtTokenProvider;
import com.example.userservice.store.model.Store;
import com.example.userservice.store.model.dto.StoreDto;
import com.example.userservice.store.service.StoreService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;


@Slf4j
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {
    private final String COOKIE_NAME = "ATOKEN";
    private final Duration MAX_AGE = Duration.ofHours(1L);

    private final StoreService storeService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public BaseResponse<String> registerStore(@RequestHeader("X-Email-Url") String emailUrl, @RequestBody StoreDto.RegistRequest dto, HttpServletResponse response) {
        Long storeId = storeService.registerStore(dto, emailUrl);
        String jwtToken = jwtTokenProvider.createToken(emailUrl, String.valueOf(storeId));
        setCookie(response, COOKIE_NAME, jwtToken, MAX_AGE);
        return BaseResponse.success("가게등록에 성공했습니다.");
    }

    @GetMapping("/getAddress")
    public BaseResponse<StoreDto.response> getAddress(@RequestHeader("X-Store-Id") String storeId) {
        Store store = storeService.getStoreById(Long.parseLong(storeId));
        StoreDto.response response = StoreDto.response.builder()
                .address(store.getAddress())
                .name(store.getName())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .phoneNumber(store.getPhoneNumber())
                .build();
        return BaseResponse.success(response);
    }
    public void setCookie(HttpServletResponse response, String name, String jwtToken, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie
                .from(name, jwtToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
        