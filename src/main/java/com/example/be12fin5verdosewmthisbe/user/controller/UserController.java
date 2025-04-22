package com.example.be12fin5verdosewmthisbe.user.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.security.JwtTokenProvider;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserDto;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserInfoDto;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserRegisterDto;
import com.example.be12fin5verdosewmthisbe.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import com.example.be12fin5verdosewmthisbe.user.model.dto.PhoneVerificationDto;
import com.example.be12fin5verdosewmthisbe.user.service.PhoneVerificationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/signup")
    public BaseResponse<UserRegisterDto.SignupResponse> signUp(@RequestBody UserRegisterDto.SignupRequest dto, HttpServletResponse response) {
        UserRegisterDto.SignupResponse signupResponse = userService.signUp(dto);
        String emailUrl = dto.getEmail();
        String jwtToken = jwtTokenProvider.createToken(emailUrl);

        ResponseCookie cookie = ResponseCookie
                .from("ATOKEN", jwtToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofHours(1L))
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return BaseResponse.success(signupResponse);
    }
    //회원가입

    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody UserDto.LoginRequest dto, HttpServletResponse response) {
        User user = userService.login(dto.getEmail(), dto.getPassword());
        String emailUrl = dto.getEmail();
        boolean isStoreRegistered = userService.isStoreRegistered(emailUrl);

        if(isStoreRegistered) {
            String storeId = userService.getStoreId(emailUrl);
            String jwtToken = jwtTokenProvider.createToken(emailUrl, storeId);

            ResponseCookie cookie = ResponseCookie
                    .from("ATOKEN", jwtToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(Duration.ofHours(1L))
                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        else{
            String jwtToken = jwtTokenProvider.createToken(emailUrl);

            ResponseCookie cookie = ResponseCookie
                    .from("ATOKEN", jwtToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(Duration.ofHours(1L))
                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return BaseResponse.success("로그인에 성공했습니다.");
    }
    // 로그인
    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie
                .from("ATOKEN", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofHours(1L))
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return BaseResponse.success("로그아웃 되었습니다.");
    }
    // 로그아웃

    @GetMapping("/searchinfo")
    public BaseResponse<UserInfoDto.SearchResponse> searchInfo(HttpServletRequest request) {
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
        UserInfoDto.SearchResponse dto = userService.searchUserInfo(emailUrl);
        return BaseResponse.success(dto);
    }
    // 유저 정보 조회

    @PutMapping("/updateinfo")
    public BaseResponse<String> updateInfo(@RequestBody UserInfoDto.UpdateRequest dto) {
        String result = userService.updateUserInfo(dto);
        return BaseResponse.success(result);
    }
    // 유저 정보 수정


    @DeleteMapping("/delete")
    public BaseResponse<String> deleteUser(HttpServletRequest request, HttpServletResponse response) {
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

        String result = userService.deleteUser(emailUrl);

        removeCookie(response);
        return BaseResponse.success(result);
    }
    // 유저 탈퇴

    @PutMapping("/updatepassword")
    public BaseResponse<String> updatePassword(@RequestBody UserInfoDto.PasswordRequest dto) {
        String result = userService.updatePassword(dto);
        return BaseResponse.success(result);
    }
    // 새로운 비밀번호 만들기

    @PostMapping("/smssend")
    public BaseResponse<String> sendCode(@RequestBody PhoneVerificationDto.SmsSendRequestDto dto) {
        phoneVerificationService.sendCertificationCode(dto.getPhoneNum());
        return BaseResponse.success("인증번호 전송 완료");
    }

    @PostMapping("/phoneverify")
    public BaseResponse<String> verifyCode(@RequestBody PhoneVerificationDto.VerifyRequestDto dto) {
        phoneVerificationService.verifyCertificationCode(dto.getPhoneNum(), dto.getCode());
        return BaseResponse.success("인증 성공");
    }

    @GetMapping("/isRegistered")
    public BaseResponse<String> isRegistered(HttpServletRequest request,HttpServletResponse response) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ATOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
            String email = jwtTokenProvider.getEmailFromToken(token);
            String tokenStoreId = jwtTokenProvider.getStoreIdFromToken(token);
            if(tokenStoreId == null) {
                throw new CustomException(ErrorCode.STORE_NOT_REGISTER);
            }
            String storeId = userService.getStoreId(email);
            if(!storeId.equals(tokenStoreId)) {
                // 토큰의 storeId와 DB의 storeId가 다를때
                removeCookie(response);
                throw new CustomException(ErrorCode.TOKEN_NOT_VALIDATE);
            }
        } else {
            // 토큰 없으면
            throw new CustomException(ErrorCode.TOKEN_NOT_VALIDATE);
        }
        return BaseResponse.success("ok");
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
}
        