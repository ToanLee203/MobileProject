package com.toan.expensemanager.data.model;

public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;

    public RegisterRequest(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    // Getters (nếu cần)
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
}
