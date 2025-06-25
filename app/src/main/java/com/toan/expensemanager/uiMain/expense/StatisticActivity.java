package com.toan.expensemanager.uiMain.expense;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.toan.expensemanager.R;
import com.toan.expensemanager.data.api.ApiService;
import com.toan.expensemanager.data.api.RetrofitClient;
import com.toan.expensemanager.data.model.Category;
import com.toan.expensemanager.data.model.Expense;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticActivity extends AppCompatActivity {
    TextView tvFromDate, tvToDate, tvResultTitle;
    Button btnStatistic;
    LinearLayout layoutResult;
    Spinner spinnerCategory;

    String fromDate = "", toDate = "";
    int selectedCategoryId = -1;

    List<Category> categoryList = new ArrayList<>();
    ArrayAdapter<Category> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        btnStatistic = findViewById(R.id.btnStatistic);
        layoutResult = findViewById(R.id.layoutResult);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        spinnerCategory = findViewById(R.id.spinnerCategoryStat); // ✅ đúng ID layout

        tvFromDate.setOnClickListener(v -> pickDate(true));
        tvToDate.setOnClickListener(v -> pickDate(false));

        loadCategories();

        btnStatistic.setOnClickListener(v -> {
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                fetchStatistics();
            } else {
                Toast.makeText(this, "Vui lòng chọn đủ ngày", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = categoryList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
