package com.vtn.Yame.Enum;

public enum RoleEnum {
    CUSTOMER,
    ADMIN;

    public static RoleEnum fromString(String role) {
        if (role == null) {
            return RoleEnum.CUSTOMER;
        }
        try {
            return RoleEnum.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RoleEnum.CUSTOMER;
        }
    }
}
