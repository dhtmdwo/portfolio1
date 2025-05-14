package com.example.userservice.user.repository;

import com.example.userservice.user.model.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
    Optional<PhoneVerification> findTopByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
