package com.example.be12fin5verdosewmthisbe.user.sms.service;

import com.example.be12fin5verdosewmthisbe.common.SmsCertificationUtil;
import com.example.be12fin5verdosewmthisbe.user.sms.model.SmsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsCertificationUtil smsCertificationUtil;

    public void SendSms(SmsDto.SmsRequestDto smsRequestDto) {
        String phoneNum = smsRequestDto.getPhoneNum();
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        smsCertificationUtil.sendSMS(phoneNum, certificationCode); // SMS 인증 유틸리티를 사용하여 SMS 발송
    }
}
