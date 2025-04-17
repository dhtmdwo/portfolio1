package com.example.be12fin5verdosewmthisbe.order.repository;

import com.example.be12fin5verdosewmthisbe.order.model.Order;
import com.example.be12fin5verdosewmthisbe.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStoreId(Long storeId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.store s " +
            "WHERE s.id = :storeId " +
            "AND FUNCTION('DATE', o.createdAt) = :today")
    List<Order> findTodayOrderByStoreIdx(
            @Param("storeId") String storeId,
            @Param("today") LocalDate today
    );

}
        