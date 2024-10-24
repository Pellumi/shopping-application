package com.example.mobiledesignproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.adapter.OrderAdapter;
import com.example.mobiledesignproject.api.GetOrderApiService;
import com.example.mobiledesignproject.model.LoginResponse;
import com.example.mobiledesignproject.model.Order;
import com.example.mobiledesignproject.model.OrderResponse;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderedItemsActivity extends AppCompatActivity {

    ImageView backToAccount;
    LinearLayout completedNav, pendingNav, returnedNav, completedTab, pendingTab,returnedTab, emptyCompleted, emptyPending, emptyReturned;
    RecyclerView completedView, pendingView, returnedView;
    View completedLine, pendingLine, returnedLine;
    TextView completeText, pendingText, returnedText;
    Intent intent;
    Context context = OrderedItemsActivity.this;
    SharedPreferences shardPreferences;
    Gson gson = new Gson();
    OrderAdapter completeAdapter, pendingAdapter, returnedAdapter;
    Button completeToHome, pendingToHome, returnedToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ordered_items);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColor));

        shardPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String loginResponseJson = shardPreferences.getString("loginResponse", "");
        LoginResponse loginResponse = gson.fromJson(loginResponseJson, LoginResponse.class);

        String user_id = loginResponse.getUserDetails().getUserId();

        backToAccount = findViewById(R.id.back_arrow);
        completedNav = findViewById(R.id.order_completed);
        pendingNav = findViewById(R.id.order_pending);
        returnedNav = findViewById(R.id.order_returned);
        completedTab = findViewById(R.id.completed_orders);
        pendingTab = findViewById(R.id.pending_orders);
        returnedTab = findViewById(R.id.returned_orders);
        completedLine = findViewById(R.id.complete_line);
        pendingLine = findViewById(R.id.pending_line);
        returnedLine = findViewById(R.id.returned_line);
        completeText = findViewById(R.id.complete_text);
        pendingText = findViewById(R.id.pending_text);
        returnedText = findViewById(R.id.returned_text);
        emptyCompleted = findViewById(R.id.empty_complete_orders);
        emptyPending = findViewById(R.id.empty_pending_orders);
        emptyReturned = findViewById(R.id.empty_returned_orders);
        completedView = findViewById(R.id.completed_view);
        pendingView = findViewById(R.id.pending_view);
        returnedView = findViewById(R.id.returned_view);
        completeToHome = findViewById(R.id.complete_to_main);
        pendingToHome = findViewById(R.id.pending_to_main);
        returnedToHome = findViewById(R.id.returned_to_main);

        backToAccount.setOnClickListener(v -> {
            intent = new Intent(context, MainActivity.class);
            intent.setData(Uri.parse("myapp://navigation_account"));
            context.startActivity(intent);
        });

        completedNav.setOnClickListener(v -> {
            pendingText.setTextColor(Color.BLACK);
            returnedText.setTextColor(Color.BLACK);
            completeText.setTextColor(getResources().getColor(R.color.accent_color));

            pendingLine.setVisibility(View.GONE);
            returnedLine.setVisibility(View.GONE);
            completedLine.setVisibility(View.VISIBLE);

            pendingTab.setVisibility(View.GONE);
            returnedTab.setVisibility(View.GONE);
            completedTab.setVisibility(View.VISIBLE);
        });

        pendingNav.setOnClickListener(v -> {
            completeText.setTextColor(Color.BLACK);
            returnedText.setTextColor(Color.BLACK);
            pendingText.setTextColor(getResources().getColor(R.color.accent_color));

            completedLine.setVisibility(View.GONE);
            returnedLine.setVisibility(View.GONE);
            pendingLine.setVisibility(View.VISIBLE);

            completedTab.setVisibility(View.GONE);
            returnedTab.setVisibility(View.GONE);
            pendingTab.setVisibility(View.VISIBLE);
        });

        returnedNav.setOnClickListener(v -> {
            completeText.setTextColor(Color.BLACK);
            pendingText.setTextColor(Color.BLACK);
            returnedText.setTextColor(getResources().getColor(R.color.accent_color));

            completedLine.setVisibility(View.GONE);
            pendingLine.setVisibility(View.GONE);
            returnedLine.setVisibility(View.VISIBLE);

            completedTab.setVisibility(View.GONE);
            pendingTab.setVisibility(View.GONE);
            returnedTab.setVisibility(View.VISIBLE);
        });

        fetchOrders(user_id, completedView, pendingView, returnedView);

        completeToHome.setOnClickListener(v -> {
            intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });

        pendingToHome.setOnClickListener(v -> {
            intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });

        returnedToHome.setOnClickListener(v -> {
            intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });
    }

    private void fetchOrders (String userId, RecyclerView completeView, RecyclerView pendingView, RecyclerView returnedView) {
        RetrofitClient.getClient().create(GetOrderApiService.class)
                .getOrders(userId)
                .enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OrderResponse> call, @NonNull Response<OrderResponse> response) {
                        if(response.isSuccessful() && response.body() != null){
                            List<Order> deliveredList = new ArrayList<>();
                            List<Order> pendingList = new ArrayList<>();
                            List<Order> returnedList = new ArrayList<>();

                            for (Map.Entry<String, Order> entry : response.body().getOrders().entrySet()) {
                                Order order = entry.getValue();
                                order.setName(entry.getKey());

                                switch (order.getStatus()) {
                                    case "delivered":
                                        deliveredList.add(order);
                                        break;
                                    case "pending":
                                    case "on-delivery":
                                    case "at-doorstep":
                                        pendingList.add(order);
                                        break;
                                    case "returned":
                                        returnedList.add(order);
                                        break;
                                }

                                completeAdapter = new OrderAdapter(deliveredList);
                                displayView(completeView, completeAdapter);

                                pendingAdapter = new OrderAdapter(pendingList);
                                displayView(pendingView, pendingAdapter);

                                returnedAdapter = new OrderAdapter(returnedList);
                                displayView(returnedView, returnedAdapter);

                                toggleView(deliveredList, completeView, emptyCompleted);
                                toggleView(pendingList, pendingView, emptyPending);
                                toggleView(returnedList, returnedView, emptyReturned);
                            }
                        } else {
                            Toast.makeText(context, "Failed to retrieve cart" + response.message(), Toast.LENGTH_SHORT).show();
                            Log.e("CartFragmentError", "Other Error: " + response.code() + " - " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                        Log.e("OrderedItemsActivity", "Error fetching orders: " + t.getMessage(), t);
                    }
                });
    }

    private void displayView(RecyclerView recyclerView, OrderAdapter orderAdapter){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(orderAdapter);
    }

    private void toggleView(List<Order> orderList, RecyclerView recyclerView, LinearLayout linearLayout){
        if(orderList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }
    }
}