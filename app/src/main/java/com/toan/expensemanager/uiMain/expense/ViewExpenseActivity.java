package com.toan.expensemanager.uiMain.expense;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.toan.expensemanager.R;

import java.util.Calendar;

public class ViewExpenseActivity extends AppCompatActivity {
    TextView tvSelectedDate;
    LinearLayout expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        expenseList = findViewById(R.id.expensesContainer);  // sửa đúng id

        // Khởi tạo ngày hiện tại hiển thị trên tvSelectedDate
        Calendar c = Calendar.getInstance();
        String currentDate = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        tvSelectedDate.setText(currentDate);
        showExpensesForDate(currentDate);

        tvSelectedDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                tvSelectedDate.setText(date);
                showExpensesForDate(date);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    void showExpensesForDate(String date) {
        expenseList.removeAllViews();

        if (date.equals("15/5/2025")) {
            addExpenseView("Ăn trưa", "Cơm gà xối mỡ", "50,000đ");
            addExpenseView("Cà phê", "Cà phê sữa đá", "25,000đ");
        } else if (date.equals("1/6/2025")) {
            addExpenseView("Ăn sáng", "Bún bò Huế", "100,000đ");
            addExpenseView("Đi lại", "Taxi sân bay", "150,000đ");
            addExpenseView("Mua sắm", "Quần áo", "250,000đ");
        } else {
            Toast.makeText(this, "Không có chi tiêu ngày này", Toast.LENGTH_SHORT).show();
        }
    }

    void addExpenseView(String title, String description, String amount) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setPadding(16, 16, 16, 16);
        container.setBackgroundResource(R.drawable.viewex_card_background);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        container.setLayoutParams(params);

        // Container cho title + description, weight=1
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textContainer.setLayoutParams(textParams);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(0xFF333333);
        textContainer.addView(tvTitle);

        if (description != null && !description.isEmpty()) {
            TextView tvDesc = new TextView(this);
            tvDesc.setText(description);
            tvDesc.setTextSize(14);
            tvDesc.setTextColor(0xFF666666);
            textContainer.addView(tvDesc);
        }

        TextView tvAmount = new TextView(this);
        tvAmount.setText(amount);
        tvAmount.setTextSize(16);
        tvAmount.setTextColor(0xFFFF3B30);
        tvAmount.setTypeface(null, android.graphics.Typeface.BOLD);

        container.addView(textContainer);
        container.addView(tvAmount);

        expenseList.addView(container);
    }
}
