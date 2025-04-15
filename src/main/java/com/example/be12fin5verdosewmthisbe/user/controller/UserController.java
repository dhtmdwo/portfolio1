package com.example.be12fin5verdosewmthisbe.user.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserDto;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserRegisterDto;
import com.example.be12fin5verdosewmthisbe.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/singUp")
    public ResponseEntity<BaseResponse<String>> singUp(@RequestBody UserRegisterDto.SignupRequest dto) {

        return ResponseEntity.ok(BaseResponse.success("회원가입에 성공하셨습니다"));
    }
    //회원가입

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<String>> login(@RequestBody UserDto.LoginRequest dto) {
        String jwtToken = userService.login(dto.getEmail(), dto.getPassword());

        ResponseCookie cookie = ResponseCookie
                .from("ATOKEN", jwtToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofHours(1L))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(BaseResponse.success("로그인에 성공했습니다."));
    }
    // 로그인
}
        