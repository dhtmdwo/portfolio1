package com.example.be12fin5verdosewmthisbe.user.model.dto;

import lombok.Getter;

public class PhoneVerificationDto {

    @Getter
    public static class SmsSendRequestDto{
        private String phoneNum;
    }
    @Getter
    public static class VerifyRequestDto{
        private String phoneNum;
        private String code;
    }
}
