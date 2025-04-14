package com.example.be12fin5verdosewmthisbe.payment.service;

import com.example.be12fin5verdosewmthisbe.payment.model.dto.PaymentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PaymentService {
    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // Content-Type 변경

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>(); // MultiValueMap 사용
        body.add("imp_key", apiKey);
        body.add("imp_secret", apiSecret);
        System.out.println(body);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.iamport.kr/users/getToken", request, Map.class
        );

        return ((Map<String, String>) response.getBody().get("response")).get("access_token");
    }

    public PaymentDto.PaymentData getPaymentData(String impUid, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.iamport.kr/payments/" + impUid,
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> paymentInfo = (Map<String, Object>) response.getBody().get("response");

        return new PaymentDto.PaymentData(
                (String) paymentInfo.get("merchant_uid"),
                ((Number) paymentInfo.get("amount")).intValue()
        );
    }
}
        