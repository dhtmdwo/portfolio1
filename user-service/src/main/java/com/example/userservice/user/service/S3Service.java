package com.example.userservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner presigner;
    @Value("${aws.s3.bucket}")
    private String bucket;

    public String generatePresignedUrl(String key, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        // PresignedPutObjectRequest는 presigner가 생성
        PresignedPutObjectRequest presignedReq =
                presigner.presignPutObject(b -> b
                        .signatureDuration(Duration.ofMinutes(5))    // ← java.time.Duration 사용
                        .putObjectRequest(objectRequest)
                );

        return presignedReq.url().toString();
    }
}