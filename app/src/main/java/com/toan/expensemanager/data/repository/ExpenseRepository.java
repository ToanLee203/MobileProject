package com.toan.expensemanager.data.repository;

import com.toan.expensemanager.data.api.ApiService;
import com.toan.expensemanager.data.api.RetrofitClient;
import com.toan.expensemanager.data.model.Expense;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseRepository {

    public interface ExpenseCallback {
        void onSuccess(List<Expense> expenses);
        void onError(String error);
    }

    public void getExpensesByUserAndDate(int userId, String date, ExpenseCallback callback) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<Expense>> call = api.getExpensesByUserAndDate(userId, date);

        call.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi phản hồi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                callback.onError("Lỗi: " + t.getMessage());
            }
        });
    }
}
