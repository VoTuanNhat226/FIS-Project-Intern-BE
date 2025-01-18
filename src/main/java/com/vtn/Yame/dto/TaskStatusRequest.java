package com.vtn.Yame.dto;

public class TaskStatusRequest {
    private String status;

    // Constructor
    public TaskStatusRequest() {
    }

    public TaskStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
