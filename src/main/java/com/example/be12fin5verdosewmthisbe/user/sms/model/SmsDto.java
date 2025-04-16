package com.example.be12fin5verdosewmthisbe.user.sms.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

public class SmsDto {

    @Getter
    public static class SmsRequestDto{
        @NotEmpty(message = "휴대폰 번호를 입력해주세요")
        private String phoneNum;
    }
}
