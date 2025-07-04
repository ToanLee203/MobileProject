package com.toan.expensemanager.uiMain.expense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.toan.expensemanager.R;
import com.toan.expensemanager.data.api.ApiService;
import com.toan.expensemanager.data.api.RetrofitClient;
import com.toan.expensemanager.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {
    private TextView tvFullName, tvEmail;
    private Button btnUpdate;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Khởi tạo các view
        ImageView avatar = findViewById(R.id.imgAvatar);
        avatar.setClipToOutline(true);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        btnUpdate = findViewById(R.id.btnUpdateAccount);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Đánh dấu mục Trợ giúp là đang chọn
        bottomNavigationView.setSelectedItemId(R.id.nav_help);

        // Xử lý nút Thay đổi thông tin
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, UpdateAccountActivity.class);
            startActivity(intent);
        });

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
                // Đã ở UserProfileActivity, không cần làm gì
                return true;
            }
            return false;
        });

        // Lấy thông tin người dùng
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            return;
        }

        getUserInfo(userId);
    }

    private void getUserInfo(int userId) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<User> call = api.getUserById(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvFullName.setText("Họ tên: " + user.getFullName());
                    tvEmail.setText("Email: " + user.getEmail());
                } else {
                    Toast.makeText(UserProfileActivity.this, "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}