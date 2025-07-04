package com.toan.expensemanager.uiMain.expense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.toan.expensemanager.R;
import com.toan.expensemanager.uiMain.login.Login;

public class MainOptionActivity extends AppCompatActivity {
    private LinearLayout btnViewExpense, btnStatistic, btnAddExpense, btnLogout, btnAccountInfo;
    private ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainoptionactivity);

        // Khởi tạo các view
        ImageView avatar = findViewById(R.id.imgAvatar);
        avatar.setClipToOutline(true);
        btnViewExpense = findViewById(R.id.btnViewExpense);
        btnStatistic = findViewById(R.id.btnStatistic);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnLogout = findViewById(R.id.btnLogout);
        imgAvatar = findViewById(R.id.imgAvatar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Đánh dấu mục Home là đang chọn
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Lời chào người dùng
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String fullName = prefs.getString("fullName", "bạn");
        tvGreeting.setText("Xin chào " + fullName + "!");

        // Xử lý BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Đã ở MainOptionActivity, không cần làm gì
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

        // Gắn sự kiện
        btnAddExpense.setOnClickListener(v -> startActivity(new Intent(this, AddExpenseActivity.class)));
        btnViewExpense.setOnClickListener(v -> startActivity(new Intent(this, ViewExpenseActivity.class)));
        btnStatistic.setOnClickListener(v -> startActivity(new Intent(this, StatisticActivity.class)));

        tvGreeting.setOnClickListener(v -> {
            Intent intent = new Intent(MainOptionActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
        imgAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(MainOptionActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Xóa thông tin đăng nhập
            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            // Chuyển về màn Login
            Intent intent = new Intent(MainOptionActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}