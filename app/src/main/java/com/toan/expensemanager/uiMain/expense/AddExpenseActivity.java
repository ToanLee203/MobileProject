package com.toan.expensemanager.uiMain.expense;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.toan.expensemanager.data.model.ExpenseRequest;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etAmount, etDescription;
    private TextView tvDate, tvTitle;
    private Spinner spinnerCategory;
    private Button btnSaveExpense;

    private List<Category> categoryList = new ArrayList<>();
    private int selectedCategoryId = -1;
    private String selectedDate = "";

    private boolean isEdit = false;
    private int expenseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Ánh xạ view
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        tvDate = findViewById(R.id.tvDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);
        tvTitle = findViewById(R.id.tvTitle); // TextView tiêu đề

        // Nhận intent
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("isEdit", false);
        expenseId = intent.getIntExtra("expenseId", -1);

        // Định dạng ngày mặc định là hôm nay
        Calendar c = Calendar.getInstance();
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.getTime());
        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(c.getTime()));

        // Đổi tiêu đề theo chế độ
        if (isEdit) {
            tvTitle.setText("Chỉnh sửa chi tiêu");
            btnSaveExpense.setText("Cập nhật");
        } else {
            tvTitle.setText("Thêm mới chi tiêu");
            btnSaveExpense.setText("Lưu chi tiêu");
        }

        // Chọn ngày
        tvDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(year, month, dayOfMonth);
                selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCal.getTime());
                tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCal.getTime()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSaveExpense.setOnClickListener(v -> saveExpense());
        loadCategories();
    }

    private void loadCategories() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(AddExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryList);
                    spinnerCategory.setAdapter(adapter);

                    spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategoryId = categoryList.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });

                    // Nếu là chế độ sửa → load dữ liệu chi tiêu
                    if (isEdit && expenseId != -1) {
                        loadExpenseById(expenseId);
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AddExpenseActivity.this, "Lỗi load danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExpenseById(int id) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getExpenseById(id).enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Expense expense = response.body();
                    etAmount.setText(String.valueOf((int) expense.getAmount()));
                    etDescription.setText(expense.getDescription());

                    selectedDate = expense.getDate();
                    tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(parseDate(selectedDate)));

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getId() == expense.getCategoryId()) {
                            spinnerCategory.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                Toast.makeText(AddExpenseActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }

    private void saveExpense() {
        String amountStr = etAmount.getText().toString();
        String note = etDescription.getText().toString();

        if (amountStr.isEmpty() || selectedDate.isEmpty() || selectedCategoryId == -1) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        ExpenseRequest expense = new ExpenseRequest(amount, note, selectedDate, selectedCategoryId, userId);
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        if (isEdit) {
            api.updateExpense(expenseId, expense).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddExpenseActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddExpenseActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddExpenseActivity.this, "Lỗi kết nối khi cập nhật", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            api.createExpense(expense).enqueue(new Callback<Expense>() {
                @Override
                public void onResponse(Call<Expense> call, Response<Expense> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddExpenseActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddExpenseActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Expense> call, Throwable t) {
                    Toast.makeText(AddExpenseActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
