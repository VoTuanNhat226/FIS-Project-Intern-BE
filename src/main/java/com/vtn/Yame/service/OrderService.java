package com.vtn.Yame.service;

import com.vtn.Yame.Enum.OrderStatusEnum;
import com.vtn.Yame.dto.CreateOrderRequestDTO;
import com.vtn.Yame.entity.Order;
import com.vtn.Yame.entity.User;
import com.vtn.Yame.repository.OrderDetailRepository;
import com.vtn.Yame.repository.OrderRepository;
import com.vtn.Yame.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
    }

    public List<Order> getAllOrders() {
       return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
    }

    public List<Order> getOrdersByStatus(OrderStatusEnum status) {
        return orderRepository.findByStatus(status);
    }

    public Order createOrder(CreateOrderRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + request.getUserId()));

        Order order = new Order();
        order.setStatus(request.getStatus());
        order.setPaymentMethodEnum(request.getPaymentMethodEnum());
        order.setUser(user);

        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }


    public Order getOrderWithDetails(Long orderId) {
        return orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        existingOrder.setUpdatedAt(LocalDateTime.now());
        existingOrder.setUser(updatedOrder.getUser());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setPaymentMethodEnum(updatedOrder.getPaymentMethodEnum());

        return orderRepository.save(existingOrder);
    }

    // Doanh thu theo sản phẩm
    public List<Map<String, Object>> getRevenueByProduct() {
        return orderDetailRepository.getRevenueByProduct();
    }

    // Doanh thu theo tháng
    public List<Map<String, Object>> getRevenueByMonth() {
        return orderDetailRepository.getRevenueByMonth();
    }

    // Doanh thu theo quý
    public List<Map<String, Object>> getRevenueByQuarter() {
        return orderDetailRepository.getRevenueByQuarter();
    }

    public Double getTotalPriceByOrderId(Long orderId) {
        return orderDetailRepository.calculateTotalPriceByOrderId(orderId);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Integer getCountOrders() {
        return orderRepository.countOrders();
    }
}
