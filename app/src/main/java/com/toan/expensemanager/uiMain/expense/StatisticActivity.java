package com.toan.expensemanager.uiMain.expense;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.toan.expensemanager.R;

import java.util.*;

public class StatisticActivity extends AppCompatActivity {
    TextView tvFromDate, tvToDate, tvResultTitle;
    Button btnStatistic;
    LinearLayout layoutResult;

    String fromDate = "", toDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        btnStatistic = findViewById(R.id.btnStatistic);
        layoutResult = findViewById(R.id.layoutResult);
        tvResultTitle = findViewById(R.id.tvResultTitle);

        tvFromDate.setOnClickListener(v -> pickDate(true));
        tvToDate.setOnClickListener(v -> pickDate(false));

        btnStatistic.setOnClickListener(v -> {
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                layoutResult.removeAllViews();

                if (fromDate.equals(toDate)) {
                    // Thống kê trong 1 ngày cụ thể
                    showDailyExpenses();
                } else {
                    // Thống kê từ ngày đến ngày
                    showSummaryExpenses();
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn đủ ngày", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void pickDate(boolean isFrom) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            if (isFrom) {
                fromDate = date;
                tvFromDate.setText(date);
            } else {
                toDate = date;
                tvToDate.setText(date);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    void showDailyExpenses() {
        String[] expenses = {
                "Ăn sáng - 30,000đ",
                "Ăn trưa - 50,000đ",
                "Mua đồ ăn vặt - 25,000đ",
                "Cà phê - 35,000đ"
        };
        for (String e : expenses) {
            TextView tv = new TextView(this);
            tv.setText("• " + e);
            tv.setTextSize(16);
            tv.setTextColor(0xFF333333);
            tv.setPadding(0, 8, 0, 8);
            layoutResult.addView(tv);
        }
    }

    void showSummaryExpenses() {
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("Ăn uống", 2000000);
        summary.put("Mua sắm", 1000000);
        summary.put("Nhà trọ", 2000000);

        int total = 0;
        for (int val : summary.values()) total += val;

        TextView totalTv = new TextView(this);
        totalTv.setText("Tổng chi: " + String.format("%,d", total) + "đ");
        totalTv.setTextSize(17);
        totalTv.setTextColor(0xFF1C1C1E);
        totalTv.setPadding(0, 8, 0, 24);
        layoutResult.addView(totalTv);

        for (Map.Entry<String, Integer> entry : summary.entrySet()) {
            TextView tv = new TextView(this);
            tv.setText("• " + entry.getKey() + ": " + String.format("%,d", entry.getValue()) + "đ");
            tv.setTextSize(16);
            tv.setTextColor(0xFF333333);
            tv.setPadding(0, 6, 0, 6);
            layoutResult.addView(tv);
        }
    }
}
