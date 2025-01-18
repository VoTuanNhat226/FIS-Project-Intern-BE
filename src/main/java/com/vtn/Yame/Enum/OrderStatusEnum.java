package com.vtn.Yame.Enum;

public enum OrderStatusEnum {
    Pending,    //Chờ xử lý
    Processing, //Đang xử lý
    Confirmed,  //Đã xác nhận
    Shipping,   //Đang giao
    Delivered,  //Đã giao
    Completed,  //Hoàn thành
    Cancelled,  //Đã hủy
    Refunded;   //Đã hoàn tiền

    public static OrderStatusEnum fromString(String status) {
        if (status == null) {
            return OrderStatusEnum.Pending;
        }
        try {
            return OrderStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OrderStatusEnum.Pending;
        }
    }
}
