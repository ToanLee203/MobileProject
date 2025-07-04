package com.toan.expensemanager.uiMain.expense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.toan.expensemanager.R;
import com.toan.expensemanager.data.api.ApiService;
import com.toan.expensemanager.data.api.RetrofitClient;
import com.toan.expensemanager.data.model.UpdateUserRequest;
import com.toan.expensemanager.data.model.User;
import com.toan.expensemanager.uiMain.login.Login;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAccountActivity extends AppCompatActivity {
    private EditText edtEmail, edtOldPassword, edtNewPassword;
    private TextView tvFullName;
    private Button btnUpdate;
    private ApiService api;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        // Ánh xạ view
        tvFullName = findViewById(R.id.tvFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnUpdate = findViewById(R.id.btnUpdateAccount);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Đánh dấu mục Trợ giúp là đang chọn
        bottomNavigationView.setSelectedItemId(R.id.nav_help);

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

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        api = RetrofitClient.getInstance().create(ApiService.class);

        // Gọi API để hiển thị thông tin ban đầu
        loadUserInfo();

        // Bắt sự kiện nút Cập nhật
        btnUpdate.setOnClickListener(v -> updateUserInfo());
    }

    private void loadUserInfo() {
        api.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvFullName.setText(user.getFullName());
                    edtEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(UpdateAccountActivity.this, "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UpdateAccountActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo() {
        String newEmail = edtEmail.getText().toString().trim();
        String currentPassword = edtOldPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();

        if (newEmail.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateUserRequest request = new UpdateUserRequest(userId, newEmail, currentPassword, newPassword);
        api.updateUser(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateAccountActivity.this, "Cập nhật thành công. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();

                    // Xoá thông tin người dùng đã lưu
                    SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                    editor.clear(); // Xoá tất cả dữ liệu
                    editor.apply();

                    // Chuyển sang màn hình đăng nhập
                    Intent intent = new Intent(UpdateAccountActivity.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String error = response.errorBody().string();
                        Toast.makeText(UpdateAccountActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(UpdateAccountActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UpdateAccountActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}