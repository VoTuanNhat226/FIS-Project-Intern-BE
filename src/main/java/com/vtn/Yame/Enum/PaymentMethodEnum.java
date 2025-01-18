package com.vtn.Yame.Enum;

public enum PaymentMethodEnum {
    Cash,
    Card,
    Transfer;

    public static PaymentMethodEnum fromString(String method) {
        if (method == null) {
            return PaymentMethodEnum.Cash;
        }
        try {
            return PaymentMethodEnum.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PaymentMethodEnum.Cash;
        }
    }
}
