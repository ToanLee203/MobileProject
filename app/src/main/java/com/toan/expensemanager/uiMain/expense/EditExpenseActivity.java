package com.toan.expensemanager.uiMain.expense;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class EditExpenseActivity extends AppCompatActivity {

    private EditText etAmount, etNote;
    private TextView tvDate;
    private Spinner spinnerCategory;
    private Button btnUpdate;

    private List<Category> categoryList = new ArrayList<>();
    private int selectedCategoryId = -1;
    private String selectedDate = "";
    private int expenseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense); // Reuse layout

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etDescription);
        tvDate = findViewById(R.id.tvDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnUpdate = findViewById(R.id.btnSaveExpense);

        btnUpdate.setText("Cập nhật");

        expenseId = getIntent().getIntExtra("expenseId", -1);
        if (expenseId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy chi tiêu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(year, month, dayOfMonth);
                selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCal.getTime());
                tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCal.getTime()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnUpdate.setOnClickListener(v -> updateExpense());

        loadCategoriesAndExpense();
    }

    private void loadCategoriesAndExpense() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(EditExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryList);
                    spinnerCategory.setAdapter(adapter);

                    loadExpenseDetails();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(EditExpenseActivity.this, "Lỗi load danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExpenseDetails() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getExpenseById(expenseId).enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Expense expense = response.body();
                    etAmount.setText(String.valueOf(expense.getAmount()));
                    etNote.setText(expense.getDescription());

                    selectedDate = expense.getDate().split("T")[0];
                    try {
                        Date parsedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate);
                        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(parsedDate));
                    } catch (Exception e) {
                        tvDate.setText(selectedDate);
                    }

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getId() == expense.getCategoryId()) {
                            spinnerCategory.setSelection(i);
                            selectedCategoryId = categoryList.get(i).getId();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                Toast.makeText(EditExpenseActivity.this, "Lỗi tải dữ liệu chi tiêu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateExpense() {
        String amountStr = etAmount.getText().toString();
        String note = etNote.getText().toString();

        if (amountStr.isEmpty() || selectedDate.isEmpty() || selectedCategoryId == -1) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        ExpenseRequest req = new ExpenseRequest(amount, note, selectedDate, selectedCategoryId, userId);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.updateExpense(expenseId, req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditExpenseActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditExpenseActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditExpenseActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
