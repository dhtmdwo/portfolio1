package com.example.orderservice.order.repository;

import com.example.orderservice.order.model.SalesComparison;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesComparisonRepository extends JpaRepository<SalesComparison, Long> {
}
