package com.vtn.Yame.repository;

import com.vtn.Yame.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query(value = "SELECT * FROM orderdetail WHERE orderid = :orderId", nativeQuery = true)
    List<OrderDetail> findByOrderDetailId(@Param("orderId") Long orderId);

    @Query(value = "SELECT * FROM orderdetail WHERE id= :Id", nativeQuery = true)
    Optional<OrderDetail> findByOrderId(@Param("Id") Long Id);

    @Query("SELECT SUM(od.totalPrice) FROM OrderDetail od WHERE od.order.id = :orderId")
    Double calculateTotalPriceByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT new map(od.product.id as productId, SUM(od.totalPrice) as totalRevenue) " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "WHERE o.status = 'Completed' " +
            "GROUP BY od.product.id")
    List<Map<String, Object>> getRevenueByProduct();

    @Query("SELECT new map(EXTRACT(YEAR FROM o.createdAt) as year, EXTRACT(MONTH FROM o.createdAt) as month, " +
            "SUM(od.totalPrice) as totalRevenue) " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "WHERE o.status = 'Completed' " +
            "GROUP BY EXTRACT(YEAR FROM o.createdAt), EXTRACT(MONTH FROM o.createdAt)")
    List<Map<String, Object>> getRevenueByMonth();

    @Query("SELECT new map(EXTRACT(YEAR FROM o.createdAt) as year, " +
            "CASE " +
            "WHEN EXTRACT(MONTH FROM o.createdAt) BETWEEN 1 AND 3 THEN 'Q1' " +
            "WHEN EXTRACT(MONTH FROM o.createdAt) BETWEEN 4 AND 6 THEN 'Q2' " +
            "WHEN EXTRACT(MONTH FROM o.createdAt) BETWEEN 7 AND 9 THEN 'Q3' " +
            "WHEN EXTRACT(MONTH FROM o.createdAt) BETWEEN 10 AND 12 THEN 'Q4' " +
            "END as quarter, " +
            "SUM(od.totalPrice) as totalRevenue) " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "WHERE o.status = 'Completed' " +
            "GROUP BY EXTRACT(YEAR FROM o.createdAt), " +
            "CASE " +
            "WHEN EXTRACT(MONTH FROM o.createdAt) BETWEEN 1 AND 3 THEN 'Q1' " +
            "WHEN EXTRACT(MONTH FROM o.createdAt) BETWEEN 4 AND 6 THEN 'Q2' " +
            "WHEN EXTRACT(MONTH FROM o.createdAt) BETWEEN 7 AND 9 THEN 'Q3' " +
            "WHEN EXTRACT(MONTH FROM o.createdAt) BETWEEN 10 AND 12 THEN 'Q4' " +
            "END")
    List<Map<String, Object>> getRevenueByQuarter();
}
