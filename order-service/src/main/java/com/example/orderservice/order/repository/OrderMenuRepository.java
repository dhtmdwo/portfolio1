package com.example.orderservice.order.repository;

import com.example.orderservice.order.model.OrderMenu;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {

        @Query("""
    SELECT m.name, SUM(om.quantity) as totalSold
    FROM OrderMenu om
    JOIN om.order o
    JOIN om.menu m
    JOIN m.category c
    WHERE c.storeId = :storeId
      AND o.createdAt BETWEEN :start AND :end
    GROUP BY m.name
    ORDER BY totalSold DESC
    """)
        List<Object[]> findBestSellingMenusByStoreAndPeriod(
                @Param("storeId") Long storeId,
                @Param("start")   Timestamp start,
                @Param("end")     Timestamp end,
                Pageable pageable       
        );


        @Query("""
        SELECT DISTINCT om FROM OrderMenu om
        JOIN FETCH om.order o
        JOIN FETCH om.menu m
        JOIN FETCH m.category c
        WHERE c.storeId = :storeId
        AND o.createdAt >= :start
        AND o.createdAt <= :end
    """)
        List<OrderMenu> findSaleMenusByStoreAndPeriod(
                @Param("storeId") Long storeId,
                @Param("start") Timestamp start,
                @Param("end") Timestamp end
        );

        boolean existsByMenuId(Long menuId);
}
