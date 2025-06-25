package com.toan.expensemanager.data.model;

public class Expense {
    private int id;
    private double amount;
    private String description;
    private String date;
    private int categoryId;
    private int userId;
    private Category category; // ✅ Thêm dòng này
    public Expense(double amount, String description, String date, int categoryId, int userId) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.categoryId = categoryId;
        this.userId = userId;
    }
    public Category getCategory() {
        return category;
    }
    // ✅ Getter và Setter cho id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // ✅ Getter và Setter cho amount
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    // ✅ Getter và Setter cho description
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // ✅ Getter và Setter cho date
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    // ✅ Getter và Setter cho categoryId
    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    // ✅ Getter và Setter cho userId
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
}

