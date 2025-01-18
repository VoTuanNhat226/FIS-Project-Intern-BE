package com.vtn.Yame.dto;

public class ForgotPasswordRequest {
    private String usernameOrEmail; // Người dùng có thể nhập username hoặc email

    // Constructor mặc định
    public ForgotPasswordRequest() {
    }

    // Constructor có tham số
    public ForgotPasswordRequest(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    // Getter
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    // Setter
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
}

