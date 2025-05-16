package com.example.userservice.user.controller;

import com.example.common.common.config.BaseResponse;
import com.example.common.common.Jwt.JwtTokenProvider;
import com.example.userservice.store.model.Store;
import com.example.userservice.user.model.User;
import com.example.userservice.user.model.dto.PhoneVerificationDto;
import com.example.userservice.user.model.dto.UserDto;
import com.example.userservice.user.model.dto.UserInfoDto;
import com.example.userservice.user.model.dto.UserRegisterDto;
import com.example.userservice.user.service.PhoneVerificationService;
import com.example.userservice.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final String COOKIE_NAME = "ATOKEN";
    private final Duration MAX_AGE = Duration.ofHours(1L);

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/signup")
    public BaseResponse<UserRegisterDto.SignupResponse> signUp(@RequestBody @Valid UserRegisterDto.SignupRequest dto, HttpServletResponse response) {
        UserRegisterDto.SignupResponse signupResponse = userService.signUp(dto);

        return BaseResponse.success(signupResponse);
    }

    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody @Valid UserDto.LoginRequest dto, HttpServletResponse response) {
        User user = userService.login(dto.getEmail(), dto.getPassword());
        Store store = user.getStore();

        if(store != null) {
            String storeId = String.valueOf(store.getId());
            String jwtToken = jwtTokenProvider.createToken(dto.getEmail(), storeId);
            setCookie(response,COOKIE_NAME,jwtToken,MAX_AGE);
        }
        else{
            String jwtToken = jwtTokenProvider.createToken(dto.getEmail());
            setCookie(response,COOKIE_NAME,jwtToken,MAX_AGE);
        }
        return BaseResponse.success("로그인에 성공했습니다.");
    }

    // 로그인
    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletResponse response) {
        removeCookie(response);
        return BaseResponse.success("로그아웃 되었습니다.");
    }
    @GetMapping("/searchinfo")
    public BaseResponse<UserInfoDto.SearchResponse> searchInfo(@RequestHeader("X-Email-Url") String emailUrl) {
        UserInfoDto.SearchResponse dto = userService.searchUserInfo(emailUrl);
        return BaseResponse.success(dto);
    }

    @PutMapping("/updateinfo")
    public BaseResponse<String> updateInfo(@RequestBody @Valid UserInfoDto.UpdateRequest dto) {
        String result = userService.updateUserInfo(dto);
        return BaseResponse.success(result);
    }

    @DeleteMapping("/delete")
    public BaseResponse<String> deleteUser(@RequestHeader("X-Email-Url") String emailUrl, HttpServletResponse response) {
        String result = userService.deleteUser(emailUrl);
        removeCookie(response);
        return BaseResponse.success(result);
    }

    @PutMapping("/updatepassword")
    public BaseResponse<String> updatePassword(@RequestBody @Valid UserInfoDto.PasswordRequest dto) {
        String result = userService.updatePassword(dto);
        return BaseResponse.success(result);
    }

    @PostMapping("/smssend")
    public BaseResponse<String> sendCode(@RequestBody @Valid PhoneVerificationDto.SmsSendRequestDto dto) {
        String code = phoneVerificationService.sendCertificationCode(dto.getPhoneNum());
        return BaseResponse.success(code);
    }

    @PostMapping("/phoneverify")
    public BaseResponse<String> verifyCode(@RequestBody @Valid PhoneVerificationDto.VerifyRequestDto dto) {
        phoneVerificationService.verifyCertificationCode(dto.getPhoneNum(), dto.getCode());
        return BaseResponse.success("인증 성공");
    }

    public void removeCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie
                .from("ATOKEN", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(0) // 쿠키 즉시 만료
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void setCookie(HttpServletResponse response, String name, String jwtToken,Duration maxAge) {
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
        