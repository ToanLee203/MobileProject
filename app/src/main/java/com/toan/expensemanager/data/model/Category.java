package com.toan.expensemanager.data.model;

public class Category {
    private int id;
    private String name;

    // Getter + Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name; // để Spinner hiển thị tên
    }
}

