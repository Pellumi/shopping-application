package com.example.mobiledesignproject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import com.example.mobiledesignproject.adapter.CheckOutAdapter;
import com.example.mobiledesignproject.adapter.CheckOutPlaceHolderAdapter;
import com.example.mobiledesignproject.api.PlaceOrderApiService;
import com.example.mobiledesignproject.api.ShowCartApiService;
import com.example.mobiledesignproject.api.SingleProductApiService;
import com.example.mobiledesignproject.model.CartResponse;
import com.example.mobiledesignproject.model.LoginResponse;
import com.example.mobiledesignproject.model.OrderRequest;
import com.example.mobiledesignproject.model.Product;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    ImageView backArrow, backToMain;
    TextView totalPrice, deliveryFee, totalFee;
    RecyclerView shipmentView;
    Button placeOrder;
    CheckOutAdapter checkOutAdapter;
    Context context = CheckoutActivity.this;
    private final UIMethods ui = new UIMethods();
    RadioGroup addressGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColor));

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String loginResponseJson = sharedPreferences.getString("loginResponse", "");
        LoginResponse loginResponse = gson.fromJson(loginResponseJson, LoginResponse.class);

        String userId = loginResponse.getUserDetails().getUserId();
        List<CartResponse> shipmentList = new ArrayList<>();

        backArrow = findViewById(R.id.back_arrow);
        backToMain = findViewById(R.id.back_to_main);
        totalPrice = findViewById(R.id.total_price);
        deliveryFee = findViewById(R.id.delivery_fee);
        totalFee = findViewById(R.id.total_fee);
        shipmentView = findViewById(R.id.shipment_list);
        addressGroup = findViewById(R.id.radioGroup);
        placeOrder = findViewById(R.id.place_order);

        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setData(Uri.parse("myapp://navigation_cart"));
            startActivity(intent);
        });

        fetchCart(userId, shipmentView, shipmentList);

        backToMain.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });

        placeOrder.setOnClickListener(v -> {
            String selectedAddress = getCheckedRadioButtonText(addressGroup);
            String totalAmount = ui.removeSymbol(totalFee.getText().toString().trim());

            if (selectedAddress == null){
                ui.showLowerRegularSnackBar(context, "Please select an address from the list", R.color.danger_color, R.id.main);
            } else {
                showDialog(context, userId, totalAmount, selectedAddress);
            }
        });
    }

    private void fetchCart(String userId, RecyclerView recyclerView, List<CartResponse> cartList){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        CheckOutPlaceHolderAdapter checkOutPlaceHolderAdapter = new CheckOutPlaceHolderAdapter(context);
        recyclerView.setAdapter(checkOutPlaceHolderAdapter);
        RetrofitClient.getClient().create(ShowCartApiService.class)
                .showCart(userId)
                .enqueue(new Callback<Map<String, CartResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, CartResponse>> call, @NonNull Response<Map<String, CartResponse>> response) {
                        if(response.isSuccessful() && response.body() != null){
                            final int[] totalAmount = {0};
                            final int[] itemNumber = {0};
                            final int[] delivery = {400};
                            final int[] totalFeeAmount = {0};

                            Map<String, CartResponse> cartMap = response.body();
                            for(Map.Entry<String, CartResponse> entry : cartMap.entrySet()){
                                CartResponse cartResponse = entry.getValue();
                                String productId = cartResponse.getProductId();

                                RetrofitClient.getClient().create(SingleProductApiService.class)
                                        .getProduct(productId)
                                        .enqueue(new Callback<Product>() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                                                if(response.isSuccessful() && response.body() != null){
                                                    Product product = response.body();
                                                    cartResponse.setName(product.getName());
                                                    cartResponse.setImageUrl(product.getImageUrl());

                                                    int itemAmount = cartResponse.getTotalPrice();
                                                    totalAmount[0] += itemAmount;

                                                    itemNumber[0] ++;
                                                    cartResponse.setItemNumber(itemNumber[0]);

                                                    totalFeeAmount[0] = totalAmount[0] + delivery[0];

                                                    cartList.add(cartResponse);
                                                    checkOutAdapter = new CheckOutAdapter(cartList, context);
                                                    recyclerView.setAdapter(checkOutAdapter);

                                                    totalPrice.setText(ui.formatCurrency(String.valueOf(totalAmount[0])));
                                                    deliveryFee.setText(String.valueOf(delivery[0]));
                                                    totalFee.setText(ui.formatCurrency(String.valueOf(totalFeeAmount[0])));
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                                                Log.e("CartFragment", "Error fetching product details: " + t.getMessage(), t);
                                            }
                                        });
                            }

                        } else {
                            if (response.code() == 404){
//                                emptyCart.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(context, "Failed to retrieve cart" + response.message(), Toast.LENGTH_SHORT).show();
                                Log.e("CartFragmentError", "Other Error: " + response.code() + " - " + response.message());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, CartResponse>> call, @NonNull Throwable t) {
                        Log.e("CartFragment", "Error fetching cart: " + t.getMessage(), t);
                    }
                });
    }

    private void placeOrder(String userId, String totalAmount, String address){
        placeOrder.setEnabled(false);
        OrderRequest orderRequest = new OrderRequest(totalAmount, address);
        RetrofitClient.getClient().create(PlaceOrderApiService.class)
                .placeOrder(userId, orderRequest)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if(response.isSuccessful()){
                            ui.showRegularSnackBar(context, "Your order has been placed, please wait patiently for delivery personnel", R.color.btn_acc, R.id.main);
                        } else {
                            if (response.code() == 404){
                                ui.showRegularSnackBar(context, "Failed to place order, Please try again", R.color.danger_color, R.id.main);
                                Log.e("CheckoutActivity", "Error: " + response.message());
                            } else if (response.code() == 400) {
                                ui.showRegularSnackBar(context, "Not enough stock for products", R.color.danger_color, R.id.main);
                            } else {
                                ui.showRegularSnackBar(context, "Failed to place order.", R.color.danger_color, R.id.main);
                                Log.e("CheckoutActivity", "Other Error: " + response.code() + " - " + response.message());
                            }
                        }
                        placeOrder.setEnabled(true);
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(context, "Failed to place order", Toast.LENGTH_SHORT).show();
                        Log.e("CheckoutActivity", "Error: " + t.getMessage());
                    }
                });
    }

    public static String getCheckedRadioButtonText(RadioGroup radioGroup) {
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (checkedRadioButtonId == -1) {
            return null;
        }

        RadioButton checkedRadioButton = radioGroup.findViewById(checkedRadioButtonId);

        if (checkedRadioButton != null) {
            return checkedRadioButton.getText().toString();
        } else {
            return null;
        }
    }

    private void showDialog(Context context, String userId, String totalAmount, String address){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_order);

        Button cancelOrder = dialog.findViewById(R.id.cancel_order);
        Button confirmOrder = dialog.findViewById(R.id.confirm_order);

        cancelOrder.setOnClickListener(v -> {
            dialog.dismiss();
        });

        confirmOrder.setOnClickListener(v -> {
            placeOrder(userId, totalAmount, address);
            dialog.dismiss();
        });

        dialog.show();
    }
}