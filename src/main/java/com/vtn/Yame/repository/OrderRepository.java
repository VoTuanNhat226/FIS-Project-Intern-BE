package com.vtn.Yame.repository;

import com.vtn.Yame.Enum.OrderStatusEnum;
import com.vtn.Yame.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Tìm đơn hàng theo trạng thái
    List<Order> findByStatus(OrderStatusEnum status);

    @EntityGraph(attributePaths = "orderDetails")
    Optional<Order> findWithDetailsById(Long id);

    List<Order> findByUserId(Long userId);

    @Query("SELECT COUNT(id) FROM Order ")
    Integer countOrders();
}
