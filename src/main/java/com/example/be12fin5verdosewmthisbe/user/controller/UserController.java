package com.example.be12fin5verdosewmthisbe.user.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.security.JwtTokenProvider;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserDto;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserInfoDto;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserRegisterDto;
import com.example.be12fin5verdosewmthisbe.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/singup")
    public ResponseEntity<BaseResponse<UserRegisterDto.SignupResponse>> singUp(@RequestBody UserRegisterDto.SignupRequest dto) {
        UserRegisterDto.SignupResponse signupResponse = userService.signUp(dto);
        return ResponseEntity.ok(BaseResponse.success(signupResponse));
    }
    //회원가입

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<String>> login(@RequestBody UserDto.LoginRequest dto) {
        User user = userService.login(dto.getEmail(), dto.getPassword());
        String jwtToken = jwtTokenProvider.createToken(user);

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

    @GetMapping("/searchinfo")
    public BaseResponse<UserInfoDto.SearchResponse> searchInfo(@AuthenticationPrincipal User user) {
        UserInfoDto.SearchResponse dto = userService.searchUserInfo(user.getEmail());
        return BaseResponse.success(dto);
    } // 유저 정보 조회

    @PutMapping("/updateinfo")
    public BaseResponse<String> updateInfo(@RequestBody UserInfoDto.updateRequest dto) {
        String result = userService.updateUserInfo(dto);
        return BaseResponse.success(result);
    } // 유저 정보 수정

}
        