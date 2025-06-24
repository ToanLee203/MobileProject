package com.toan.expensemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout btnViewExpense, btnStatistic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainoptionactivity);

        btnViewExpense = findViewById(R.id.btnViewExpense);
        btnStatistic = findViewById(R.id.btnStatistic);

        // Mở màn hình xem chi tiêu
        btnViewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.toan.expensemanager.uiMain.expense.ViewExpenseActivity.class);
                startActivity(intent);
            }
        });

        // Mở màn hình thống kê (nếu có)
        btnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(MainActivity.this, StatisticActivity.class);
                // startActivity(intent);
                // Tạm thời Toast nếu chưa có màn
                android.widget.Toast.makeText(MainActivity.this, "Tính năng đang phát triển", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
