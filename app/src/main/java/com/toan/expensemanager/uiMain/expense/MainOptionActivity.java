package com.toan.expensemanager.uiMain.expense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.toan.expensemanager.R;

public class MainOptionActivity extends AppCompatActivity {
    LinearLayout btnViewExpense, btnStatistic, btnAddExpense, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainoptionactivity);

        // Gán view
        btnViewExpense = findViewById(R.id.btnViewExpense);
        btnStatistic = findViewById(R.id.btnStatistic);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnLogout = findViewById(R.id.btnLogout); // ✅ đặt đúng trong onCreate()

        // Lời chào người dùng
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String fullName = prefs.getString("fullName", "bạn");
        tvGreeting.setText("Xin chào " + fullName + "!");

        // Gắn sự kiện
        btnAddExpense.setOnClickListener(v -> startActivity(new Intent(this, AddExpenseActivity.class)));
        btnViewExpense.setOnClickListener(v -> startActivity(new Intent(this, ViewExpenseActivity.class)));
        btnStatistic.setOnClickListener(v -> startActivity(new Intent(this, StatisticActivity.class)));

        btnLogout.setOnClickListener(v -> {
            // Xoá thông tin đăng nhập
            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            // Chuyển về màn Login
            Intent intent = new Intent(MainOptionActivity.this, com.toan.expensemanager.uiMain.login.Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

