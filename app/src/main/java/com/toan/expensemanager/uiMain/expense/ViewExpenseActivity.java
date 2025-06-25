package com.toan.expensemanager.uiMain.expense;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
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

public class ViewExpenseActivity extends AppCompatActivity {

    TextView tvSelectedDate;
    LinearLayout expenseList;
    Spinner spinnerCategory;

    String selectedDate = "";
    int selectedCategoryId = -1;

    List<Category> categoryList = new ArrayList<>();
    ArrayAdapter<Category> categoryAdapter;

    boolean isFirstSelect = true; // ✅ Tránh gọi fetchExpenses() ngay lần đầu khi spinner tự động chọn

    @Override
    protected void onResume() {
        super.onResume();
        fetchExpenses(); // Đảm bảo cập nhật lại danh sách sau khi thêm/sửa
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        expenseList = findViewById(R.id.expensesContainer);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Calendar c = Calendar.getInstance();
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.getTime());
        tvSelectedDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(c.getTime()));

        loadCategories();

        tvSelectedDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(year, month, dayOfMonth);
                selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCal.getTime());
                tvSelectedDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCal.getTime()));
                fetchExpenses();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedCategoryId = categoryList.get(pos).getId();

                if (isFirstSelect) {
                    isFirstSelect = false;
                    return;
                }

                fetchExpenses();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadCategories() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();

                    // ✅ Thêm mục "Tất cả"
                    Category allCategory = new Category();
                    allCategory.setId(-1);
                    allCategory.setName("Tất cả");
                    categoryList.add(allCategory);

                    // ✅ Thêm các mục từ API
                    categoryList.addAll(response.body());

                    categoryAdapter = new ArrayAdapter<>(ViewExpenseActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, categoryList);
                    spinnerCategory.setAdapter(categoryAdapter);

                    selectedCategoryId = -1; // Mặc định chọn "Tất cả"
                } else {
                    Toast.makeText(ViewExpenseActivity.this, "Lỗi load danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(ViewExpenseActivity.this, "Lỗi kết nối danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchExpenses() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId == -1 || selectedDate.isEmpty()) return;

        expenseList.removeAllViews();

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<Expense>> call;

        if (selectedCategoryId == -1) {
            call = api.getExpensesByUserAndDate(userId, selectedDate); // Lấy tất cả
        } else {
            call = api.getByUserDateAndCategory(userId, selectedDate, selectedCategoryId); // Lọc theo danh mục
        }

        call.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Expense> list = response.body();

                    // In log nếu cần kiểm tra
                    Log.d("DEBUG_EXPENSE", "Số bản ghi: " + list.size());

                    if (list.isEmpty()) {
                        Toast.makeText(ViewExpenseActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                    } else {
                        for (Expense e : list) {
                            addExpenseView(e);
                        }
                    }
                } else {
                    Toast.makeText(ViewExpenseActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Toast.makeText(ViewExpenseActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void addExpenseView(Expense expense) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 16, 16, 16);
        container.setBackgroundResource(R.drawable.viewex_card_background);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        container.setLayoutParams(params);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Mô tả: " + expense.getDescription());
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(0xFF333333);

        TextView tvAmount = new TextView(this);
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedAmount = formatter.format(expense.getAmount());
        tvAmount.setText("Số tiền: " + formattedAmount + "đ");
        tvAmount.setTextSize(16);
        tvAmount.setTextColor(0xFFFF3B30);

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button btnEdit = new Button(this);
        btnEdit.setText("Sửa");
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            intent.putExtra("isEdit", true);
            intent.putExtra("expenseId", expense.getId());
            startActivity(intent);
        });

        Button btnDelete = new Button(this);
        btnDelete.setText("Xóa");
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa khoản chi này không?")
                    .setPositiveButton("Có", (dialog, which) -> deleteExpense(expense.getId()))
                    .setNegativeButton("Không", null)
                    .show();
        });

        buttonLayout.addView(btnEdit);
        buttonLayout.addView(btnDelete);

        container.addView(tvTitle);
        container.addView(tvAmount);
        container.addView(buttonLayout);

        expenseList.addView(container);
    }

    private void deleteExpense(int id) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.deleteExpense(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ViewExpenseActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    fetchExpenses();
                } else {
                    Toast.makeText(ViewExpenseActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ViewExpenseActivity.this, "Lỗi mạng khi xóa", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
