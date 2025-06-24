package com.toan.expensemanager.uiMain.expense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.toan.expensemanager.R;

public class MainOptionActivity extends AppCompatActivity {
    LinearLayout btnViewExpense, btnStatistic , btnAddExpense; // sửa thành LinearLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainoptionactivity);
        // Set Tên người dùng
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String fullName = prefs.getString("fullName", "bạn");
        tvGreeting.setText("Xin chào " + fullName + "!");
        btnViewExpense = findViewById(R.id.btnViewExpense);
        btnStatistic = findViewById(R.id.btnStatistic);
        btnAddExpense = findViewById(R.id.btnAddExpense);

        btnAddExpense.setOnClickListener(v -> startActivity(new Intent(this, AddExpenseActivity.class)));
        btnViewExpense.setOnClickListener(v -> startActivity(new Intent(this, ViewExpenseActivity.class)));
        btnStatistic.setOnClickListener(v -> startActivity(new Intent(this, StatisticActivity.class)));
    }
}
