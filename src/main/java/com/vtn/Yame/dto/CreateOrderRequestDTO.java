package com.vtn.Yame.dto;

import com.vtn.Yame.Enum.OrderStatusEnum;
import com.vtn.Yame.Enum.PaymentMethodEnum;

public class CreateOrderRequestDTO {
    private OrderStatusEnum status;
    private PaymentMethodEnum paymentMethodEnum;
    private Long userId;

    public PaymentMethodEnum getPaymentMethodEnum() {
        return paymentMethodEnum;
    }

    public void setPaymentMethodEnum(PaymentMethodEnum paymentMethodEnum) {
        this.paymentMethodEnum = paymentMethodEnum;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OrderStatusEnum status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
