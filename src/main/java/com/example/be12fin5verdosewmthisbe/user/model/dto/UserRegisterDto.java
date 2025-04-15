package com.example.be12fin5verdosewmthisbe.user.model.dto;

import com.example.be12fin5verdosewmthisbe.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

public class UserRegisterDto {

    @Getter
    public static class SignupRequest {

        private String name;
        private String email;
        private String password;
        private String businessNumber;
        private String phoneNumber;
        private String ssn;
        public User toEntity(String encodedPassword) {
            User user = User.builder()
                    .name(name)
                    .email(email)
                    .password(encodedPassword)
                    .businessNumber(businessNumber)
                    .phoneNumber(phoneNumber)
                    .ssn(ssn)
                    .build();
            return user;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class SignupResponse {
        private Long id;
        private String name;
        private String message;

        public static SignupResponse from(User user) {
            return SignupResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .message("회원가입 성공")
                    .build();
        }
    }

}
