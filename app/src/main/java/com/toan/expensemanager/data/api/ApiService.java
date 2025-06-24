package com.toan.expensemanager.data.api;

import com.toan.expensemanager.data.model.Expense;
import com.toan.expensemanager.data.model.Category;
import com.toan.expensemanager.data.model.ExpenseRequest;
import com.toan.expensemanager.data.model.LoginRequest;
import com.toan.expensemanager.data.model.User;
import com.toan.expensemanager.data.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // THỒN KÊ CHI TIÊU THEO NGÀY CHI TIÊU
    @GET("api/Expense/search/user/{userId}/date/{date}")
    Call<List<Expense>> getExpensesByUserAndDate(
            @Path("userId") int userId,
            @Path("date") String date  // yyyy-MM-dd
    );

    // LẤY DANH MỤC CHI TIÊU
    @GET("api/Category")
    Call<List<Category>> getAllCategories();

    // lẤY NGÀY TRUY VẤN NGÀY VÀ DANH MỤC CHI TIÊU
    @GET("api/Expense/search/user/{userId}/date/{date}/category/{categoryId}")
    Call<List<Expense>> getByUserDateAndCategory(
            @Path("userId") int userId,
            @Path("date") String date,
            @Path("categoryId") int categoryId
    );

    // ĐĂNG NHẬP
    @POST("api/User/login")
    Call<User> login(@Body LoginRequest request);

    // THÊM MỚI CHI TIÊU
    @POST("api/expense") // endpoint phù hợp với backend
    Call<Expense> createExpense(@Body ExpenseRequest request);

    // XÓA CHI TIÊU
    @DELETE("api/Expense/{id}")
    Call<Void> deleteExpense(@Path("id") int id);
    // LẤY CHI TIÊU ĐỂ UPDATE
    @GET("api/Expense/{id}")
    Call<Expense> getExpenseById(@Path("id") int id);
    // UPDATE LẠI CHI TIÊU
    @PUT("api/Expense/{id}")
    Call<Void> updateExpense(@Path("id") int id, @Body ExpenseRequest req);


}
