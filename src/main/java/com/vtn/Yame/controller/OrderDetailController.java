package com.vtn.Yame.controller;

import com.vtn.Yame.dto.CreateOrderDetailRequestDTO;
import com.vtn.Yame.entity.OrderDetail;
import com.vtn.Yame.entity.Order;
import com.vtn.Yame.entity.Product;
import com.vtn.Yame.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orderDetail")
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @Autowired
    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @PostMapping
    public ResponseEntity<OrderDetail> createOrderDetail(@RequestBody CreateOrderDetailRequestDTO orderDetailDTO) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setQuantity(orderDetailDTO.getQuantity());

        Order order = new Order();
        order.setId(orderDetailDTO.getOrder());
        orderDetail.setOrder(order);

        Product product = new Product();
        product.setId(orderDetailDTO.getProduct());
        orderDetail.setProduct(product);

        OrderDetail createdOrderDetail = orderDetailService.createOrderDetail(orderDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderDetail);
    }

    //API sửa orderDetail theo id
    @PutMapping("/{id}")
    public OrderDetail updateOrderDetail(@PathVariable Long id, @RequestBody OrderDetail updatedOrder) {
        return orderDetailService.updateOrderDetail(id, updatedOrder);
    }

    //API xóa orderDetail theo id
    @DeleteMapping("/{orderDetailId}")
    public ResponseEntity<Map<String, String>> deleteOrderDetail(@PathVariable Long orderDetailId) {
        orderDetailService.deleteOrderDetail(orderDetailId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order detail deleted successfully");
        return ResponseEntity.ok(response);
    }
}
