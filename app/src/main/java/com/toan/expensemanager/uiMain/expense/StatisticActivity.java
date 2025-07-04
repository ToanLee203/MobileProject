package com.toan.expensemanager.uiMain.expense;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.toan.expensemanager.R;
import com.toan.expensemanager.data.api.ApiService;
import com.toan.expensemanager.data.api.RetrofitClient;
import com.toan.expensemanager.data.model.Category;
import com.toan.expensemanager.data.model.Expense;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticActivity extends AppCompatActivity {
    private TextView tvFromDate, tvToDate, tvResultTitle;
    private com.google.android.material.button.MaterialButton btnStatistic;
    private LinearLayout layoutResult;
    private Spinner spinnerCategory;

    private String fromDate = "", toDate = "";
    private int selectedCategoryId = -1;

    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<Category> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        // Ánh xạ view
        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        btnStatistic = findViewById(R.id.btnStatistic);
        layoutResult = findViewById(R.id.layoutResult);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        spinnerCategory = findViewById(R.id.spinnerCategoryStat);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Đánh dấu mục Home là đang chọn
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Xử lý BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Chuyển về MainOptionActivity
                Intent intent = new Intent(this, MainOptionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_notifications) {
                // Chuyển đến NotificationsActivity
                Intent intent = new Intent(this, NotificationsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_alert) {
                // Chuyển đến AlertActivity
                Intent intent = new Intent(this, AlertActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_help) {
                // Chuyển đến UserProfileActivity
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        // Gắn sự kiện chọn ngày
        tvFromDate.setOnClickListener(v -> pickDate(true));
        tvToDate.setOnClickListener(v -> pickDate(false));

        // Gắn sự kiện nút thống kê
        btnStatistic.setOnClickListener(v -> {
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                fetchStatistics();
            } else {
                Toast.makeText(this, "Vui lòng chọn đủ ngày", Toast.LENGTH_SHORT).show();
            }
        });

        // Gắn sự kiện chọn danh mục
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = categoryList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Tải danh mục
        loadCategories();
    }

    void pickDate(boolean isFrom) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            String formatted = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            String display = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.getTime());

            if (isFrom) {
                fromDate = formatted;
                tvFromDate.setText(display);
            } else {
                toDate = formatted;
                tvToDate.setText(display);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadCategories() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    Category all = new Category();
                    all.setId(-1);
                    all.setName("Tất cả");
                    categoryList.add(all);
                    categoryList.addAll(response.body());

                    categoryAdapter = new ArrayAdapter<>(StatisticActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryList);
                    spinnerCategory.setAdapter(categoryAdapter);
                } else {
                    Toast.makeText(StatisticActivity.this, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(StatisticActivity.this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStatistics() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId == -1) return;

        layoutResult.removeAllViews();

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<Expense>> call;

        if (selectedCategoryId == -1) {
            call = api.getExpensesByUserAndDateRange(userId, fromDate, toDate);
        } else {
            call = api.getExpensesByUserDateRangeAndCategory(userId, fromDate, toDate, selectedCategoryId);
        }

        call.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Expense> expenses = response.body();
                    if (expenses.isEmpty()) {
                        Toast.makeText(StatisticActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    displaySummary(expenses);
                } else {
                    Toast.makeText(StatisticActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Toast.makeText(StatisticActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySummary(List<Expense> expenses) {
        Map<String, Integer> summary = new HashMap<>();
        int total = 0;

        for (Expense e : expenses) {
            String key = e.getCategory().getName();
            int amount = (int) e.getAmount();
            summary.put(key, summary.getOrDefault(key, 0) + amount);
            total += amount;
        }

        TextView totalTv = new TextView(this);
        totalTv.setText("Tổng chi: " + new DecimalFormat("#,###").format(total) + "đ");
        totalTv.setTextSize(17);
        totalTv.setPadding(0, 8, 0, 24);
        layoutResult.addView(totalTv);

        for (Map.Entry<String, Integer> entry : summary.entrySet()) {
            TextView tv = new TextView(this);
            tv.setText("• " + entry.getKey() + ": " + new DecimalFormat("#,###").format(entry.getValue()) + "đ");
            tv.setTextSize(16);
            tv.setPadding(0, 6, 0, 6);
            layoutResult.addView(tv);
        }
    }
}