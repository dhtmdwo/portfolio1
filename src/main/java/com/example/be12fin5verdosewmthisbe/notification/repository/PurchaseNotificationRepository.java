package com.example.be12fin5verdosewmthisbe.notification.repository;

import com.example.be12fin5verdosewmthisbe.notification.model.PurchaseNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseNotificationRepository extends JpaRepository<PurchaseNotification, Long> {
}
