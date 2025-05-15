package com.example.userservice.email.model.dto;


import com.example.userservice.email.model.Email;
import lombok.Getter;

public class EmailDto {
    // Your code here
    @Getter
    public static class EmailRequest {
        private String emailUrl;
    }

    @Getter
    public static class EmailAuthRequest{
        private String emailUrl;
        private String code;

        public static Email toEntity(String emailUrl, String code){
            return Email.builder()
                    .emailUrl(emailUrl)
                    .code(code)
                    .build();
        }
    }


}
        