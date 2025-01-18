package com.vtn.Yame.service;

import com.vtn.Yame.Enum.OrderStatusEnum;
import com.vtn.Yame.common.InsufficientInventoryException;
import com.vtn.Yame.common.ResourceNotFoundException;
import com.vtn.Yame.entity.Inventory;
import com.vtn.Yame.entity.OrderDetail;
import com.vtn.Yame.entity.Order;
import com.vtn.Yame.entity.Product;
import com.vtn.Yame.repository.InventoryRepository;
import com.vtn.Yame.repository.OrderDetailRepository;
import com.vtn.Yame.repository.OrderRepository;
import com.vtn.Yame.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    private final InventoryService inventoryService;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository, OrderRepository orderRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, InventoryService inventoryService) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryService = inventoryService;
    }

    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        Order order = orderRepository.findById(orderDetail.getOrder().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        Product product = productRepository.findById(orderDetail.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        // Lấy sản phẩm từ bảng inventory
        Inventory inventory = inventoryRepository.findByProductId(orderDetail.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không có trong kho"));

        // Kiểm tra số lượng
        if (inventory.getQuantity() < orderDetail.getQuantity()) {
            throw new InsufficientInventoryException("Không đủ số lượng trong kho");
        }

        // Trừ số lượng
        inventory.setQuantity(inventory.getQuantity() - orderDetail.getQuantity());
        inventoryRepository.save(inventory);

        orderDetail.setOrder(order);
        orderDetail.setProduct(product);

        return orderDetailRepository.save(orderDetail);
    }


    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderDetailId(orderId);
    }

    public OrderDetail updateOrderDetail(Long id, OrderDetail updateOrderDetail) {
        OrderDetail existingOrder = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order Detail not found"));

        existingOrder.setQuantity(updateOrderDetail.getQuantity());

        return orderDetailRepository.save(existingOrder);
    }

    @Transactional
    public void deleteOrderDetail(Long orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn chi tiết"));

        Order order = orderDetail.getOrder();

        // Nếu đơn hàng chưa hoàn tất, hoàn lại số lượng vào kho
        if (!OrderStatusEnum.Completed.equals(order.getStatus())) {
            inventoryService.updateInventoryQuantity(orderDetail.getProduct().getId(), orderDetail.getQuantity());
        }

        orderDetailRepository.delete(orderDetail);
    }

}
