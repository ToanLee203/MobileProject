package com.toan.expensemanager.uiMain.expense;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.toan.expensemanager.R;

import java.util.ArrayList;
import java.util.List;

public class activity_main extends AppCompatActivity {

    private TextView tvWelcome, tvTotal;
    private Spinner spinnerFilter;
    private LinearLayout expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ✅ Sửa đúng ID mainContainer thay vì main
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Gán view
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTotal = findViewById(R.id.tvTotal);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        expenseList = findViewById(R.id.expenseList);

        // Dữ liệu lọc
        String[] filters = {"Theo ngày", "Theo tuần", "Theo tháng", "Theo năm"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        spinnerFilter.setAdapter(filterAdapter);

        // Chào người dùng
        tvWelcome.setText("Xin chào, Toàn 👋");

        // Dữ liệu chi tiêu giả lập
        List<Expense> mockData = new ArrayList<>();
        mockData.add(new Expense("Ăn trưa", "2025-05-26", "Ăn uống", 500000));
        mockData.add(new Expense("Tiền nhà", "2025-05-01", "Nhà ở", 2000000));
        mockData.add(new Expense("Xăng xe", "2025-05-20", "Đi lại", 300000));
        mockData.add(new Expense("Cafe với bạn", "2025-05-21", "Giải trí", 200000));
        mockData.add(new Expense("Mua sách", "2025-05-23", "Học tập", 200000));

        // Tổng chi
        int total = 0;
        for (Expense ex : mockData) {
            total += ex.amount;
        }
        tvTotal.setText("Tổng chi tiêu: " + total + "đ");

        // Hiển thị danh sách
        for (Expense ex : mockData) {
            TextView item = new TextView(this);
            item.setText(ex.title + " • " + ex.date + " • " + ex.amount + "đ");
            item.setPadding(32, 24, 32, 24);
            item.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            item.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 24);
            item.setLayoutParams(params);
            expenseList.addView(item);
        }
    }

    static class Expense {
        String title, date, category;
        int amount;

        public Expense(String title, String date, String category, int amount) {
            this.title = title;
            this.date = date;
            this.category = category;
            this.amount = amount;
        }
    }
}
