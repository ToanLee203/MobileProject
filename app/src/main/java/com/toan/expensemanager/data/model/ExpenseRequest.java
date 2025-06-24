package com.toan.expensemanager.data.model;

public class ExpenseRequest {
    private double amount;
    private String description;
    private String date;
    private int categoryId;
    private int userId;

    public ExpenseRequest(double amount, String description, String date, int categoryId, int userId) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

