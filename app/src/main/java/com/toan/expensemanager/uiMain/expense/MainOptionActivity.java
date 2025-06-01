package com.toan.expensemanager.uiMain.expense;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.toan.expensemanager.R;

public class MainOptionActivity extends AppCompatActivity {
    LinearLayout btnViewExpense, btnStatistic; // sửa thành LinearLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainoptionactivity);

        btnViewExpense = findViewById(R.id.btnViewExpense);
        btnStatistic = findViewById(R.id.btnStatistic);

        btnViewExpense.setOnClickListener(v -> startActivity(new Intent(this, ViewExpenseActivity.class)));
        btnStatistic.setOnClickListener(v -> startActivity(new Intent(this, StatisticActivity.class)));
    }
}
