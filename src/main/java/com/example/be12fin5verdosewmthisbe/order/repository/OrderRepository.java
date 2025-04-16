package com.example.be12fin5verdosewmthisbe.order.repository;

import com.example.be12fin5verdosewmthisbe.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStoreId(Long storeId);
}
        