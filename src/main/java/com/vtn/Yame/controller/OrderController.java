package com.vtn.Yame.controller;

import com.vtn.Yame.Enum.OrderStatusEnum;
import com.vtn.Yame.dto.CreateOrderRequestDTO;
import com.vtn.Yame.entity.OrderDetail;
import com.vtn.Yame.entity.Order;
import com.vtn.Yame.service.OrderDetailService;
import com.vtn.Yame.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    @Autowired
    public OrderController(OrderService orderService, OrderDetailService orderDetailService) {
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
    }

    //API lấy danh sách orderDetail của 1 order
    @GetMapping("/{orderId}/detail")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByOrderId(@PathVariable Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        return ResponseEntity.ok(orderDetails);
    }

    //API lấy toàn bộ order
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    //API lấy đơn hàng theo status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        try {
            OrderStatusEnum orderStatusEnum = OrderStatusEnum.valueOf(status.toUpperCase());
            List<Order> orders = orderService.getOrdersByStatus(orderStatusEnum);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        }
    }

    //API lấy đơn hàng theo ID
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    //API tạo đơn hàng mới
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequestDTO request) {
        Order newOrder = orderService.createOrder(request);
        return ResponseEntity.ok(newOrder);
    }

    //API sửa đơn hàng theo id
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        try {
            Order updated = orderService.updateOrder(id, updatedOrder);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //API xóa đơn hàng theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //API thống kê doanh thu theo sản phẩm
    @GetMapping("/revenue-by-product")
    public List<Map<String, Object>> getRevenueByProduct() {
        return orderService.getRevenueByProduct();
    }

    //API thống kê doanh thu theo tháng
    @GetMapping("/revenue-by-month")
    public List<Map<String, Object>> getRevenueByMonth() {
        return orderService.getRevenueByMonth();
    }

    //API thống kê doanh thu theo quý
    @GetMapping("/revenue-by-quarter")
    public List<Map<String, Object>> getRevenueByQuarter() {
        return orderService.getRevenueByQuarter();
    }

    //API tính tổng tiền của 1 order theo id
    @GetMapping("/total/{orderId}")
    public ResponseEntity<Double> getTotalPrice(@PathVariable Long orderId) {
        Double totalPrice = orderService.getTotalPriceByOrderId(orderId);

        if (totalPrice == null) {
            return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy đơn hàng
        }

        return ResponseEntity.ok(totalPrice);
    }

    //API lấy toàn bộ đơn hàng của user theo id
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);

        if (orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/count-order")
    public Integer getCountOrders() {
        return orderService.getCountOrders();
    }
}
