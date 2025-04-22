package com.example.be12fin5verdosewmthisbe.user.model.dto;

import com.example.be12fin5verdosewmthisbe.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserInfoDto {

    @Getter
    public static class UpdateRequest {

        private String email;
        private String currentPassword;
        private String newPassword;
        public User toEntity(String encodedPassword) {
            User user = User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .build();
            return user;
        }
    }


    @Getter
    public static class PasswordRequest {
        private String email;
        private String password;
    }

    @Getter
    public static class NewPasswordRequest {
        private String email;
        private String currentPassword;
        private String newPassword;
    }



    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class SearchResponse {
        private String phoneNumber;
        private String name;
        private String email;
        private String ssn;

        public static UserInfoDto.SearchResponse from(User user) {
            String newSsn = user.getSsn();
            if(newSsn != null && newSsn.length() > 8) {
                newSsn = newSsn.substring(0, 8) + "*".repeat(6);
            }

            return UserInfoDto.SearchResponse.builder()
                    .phoneNumber(user.getPhoneNumber())
                    .name(user.getName())
                    .email(user.getEmail())
                    .ssn(newSsn)
                    .build();
        }
    }
}
