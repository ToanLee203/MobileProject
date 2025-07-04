package com.toan.expensemanager.data.model;

public class UpdateUserRequest {
    private int userId;
    private String newEmail;
    private String currentPassword;
    private String newPassword;

    public UpdateUserRequest(int userId, String newEmail, String currentPassword, String newPassword) {
        this.userId = userId;
        this.newEmail = newEmail;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    // Getter và Setter nếu cần
}

