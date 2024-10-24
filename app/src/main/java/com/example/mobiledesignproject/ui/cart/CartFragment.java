package com.example.mobiledesignproject.ui.cart;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.CheckoutActivity;
import com.example.mobiledesignproject.MainActivity;
import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.adapter.CartAdapter;
import com.example.mobiledesignproject.adapter.CartPlaceHolderAdapter;
import com.example.mobiledesignproject.api.RemoveFromCartService;
import com.example.mobiledesignproject.api.SaveItemApiService;
import com.example.mobiledesignproject.api.ShowCartApiService;
import com.example.mobiledesignproject.api.SingleProductApiService;
import com.example.mobiledesignproject.model.CartResponse;
import com.example.mobiledesignproject.model.LoginResponse;
import com.example.mobiledesignproject.model.Product;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartFragment extends Fragment {

    TextView subTotalNum, subTotal;
    RecyclerView cartView;
    CartAdapter cartAdapter;
    LinearLayout emptyCart;
    Button checkOut, backToShop;
    Intent intent;
    private final UIMethods uiMethods = new UIMethods(getContext(), intent);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String loginResponseJson = sharedPreferences.getString("loginResponse", "");
        LoginResponse loginResponse = gson.fromJson(loginResponseJson, LoginResponse.class);

        String userId = loginResponse.getUserDetails().getUserId();
        List<CartResponse> cartList = new ArrayList<>();

        subTotalNum = view.findViewById(R.id.sub_total_num);
        subTotal = view.findViewById(R.id.total_price);
        cartView = view.findViewById(R.id.cart_view);
        emptyCart = view.findViewById(R.id.empty_cart);
        checkOut = view.findViewById(R.id.check_out);
        backToShop = view.findViewById(R.id.back_to_shop);

        fetchCart(userId, cartView, cartList);

        checkOut.setOnClickListener(v -> {
            String itemNum =  subTotalNum.getText().toString().trim();
            if (itemNum.isEmpty() || itemNum.equals("0")){
                uiMethods.showRegularSnackBar(getContext(), "You cannot proceed without an item in your cart", R.color.danger_color, R.id.fragment_cart);
            } else {
                intent = new Intent(requireActivity(), CheckoutActivity.class);
                startActivity(intent);
            }
        });

        backToShop.setOnClickListener(v -> {
            intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchCart(String userId, RecyclerView recyclerView, List<CartResponse> cartList){
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        CartPlaceHolderAdapter cartPlaceHolderAdapter = new CartPlaceHolderAdapter(getContext());
        recyclerView.setAdapter(cartPlaceHolderAdapter);

        RetrofitClient.getClient().create(ShowCartApiService.class)
                .showCart(userId)
                .enqueue(new Callback<Map<String, CartResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, CartResponse>> call, @NonNull Response<Map<String, CartResponse>> response) {
                        if(response.isSuccessful() && response.body() != null){
                            Map<String, CartResponse> cartMap = response.body();
                            final int[] totalAmount = {0};

                            List<CartResponse> tempList = new ArrayList<>();
                            CountDownLatch latch = new CountDownLatch(cartMap.size());

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

                                                    tempList.add(cartResponse);
                                                }
                                                latch.countDown();
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                                                Log.e("CartFragment", "Error fetching product details: " + t.getMessage(), t);
                                                latch.countDown();
                                            }
                                        });
                            }

                            new Thread(() -> {
                                try{
                                    latch.await();
                                    requireActivity().runOnUiThread(() -> {
                                        cartList.addAll(tempList);
                                        CartAdapter cartAdapter = new CartAdapter(cartList, getContext(), cart -> {showDialog(getContext(), userId, cart.getProductId(), cart.getName(), cartList);});
                                        recyclerView.setAdapter(cartAdapter);
                                        cartAdapter.notifyDataSetChanged();

                                        subTotal.setText(uiMethods.formatCurrency(String.valueOf(totalAmount[0])));
                                        subTotalNum.setText(cartList.size() + " item(s)");
                                    });
                                } catch (Exception e) {
                                    Log.e("CartFragment", "Interrupted while waiting for product details", e);
                                }
                            }).start();

                        } else {
                            if (response.code() == 404){
                                emptyCart.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);

                                subTotal.setText("0.0");
                                subTotalNum.setText("");
                            } else {
                                Toast.makeText(getContext(), "Failed to retrieve cart" + response.message(), Toast.LENGTH_SHORT).show();
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

    private void removeFromCart(String userId, String productId, List<CartResponse> cartList){
        Retrofit retrofit = RetrofitClient.getClient();
        RemoveFromCartService removeFromCartService = retrofit.create(RemoveFromCartService.class);

        Call<Void> call = removeFromCartService.removeFromCart(userId, productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()){
                    cartList.clear();
                    fetchCart(userId, cartView, cartList);
                    uiMethods.showRegularSnackBar(requireContext(), "Item successfully removed from cart", R.color.btn_acc, R.id.fragment_cart);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("CartFragment", "Error fetching cart: " + t.getMessage(), t);
            }
        });
    }

    private void saveItem(String userId, String productId){
        Retrofit retrofit = RetrofitClient.getClient();
        SaveItemApiService saveItemApiService = retrofit.create(SaveItemApiService.class);

        Call<Void> call = saveItemApiService.saveItem(userId, productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()){
                    uiMethods.showRegularSnackBar(requireContext(), "Item has been saved for later", R.color.btn_acc, R.id.fragment_cart);
                } else {
                    if (response.code() == 404) {
                        String errorMessage = response.message();
                        uiMethods.showRegularSnackBar(requireContext(), "Failed to add item: " + errorMessage, R.color.danger_color, R.id.fragment_cart);
                        Log.e("AddToCart", "Error: " + errorMessage);
                    } else {
                        uiMethods.showRegularSnackBar(requireContext(), "Failed to add item, Please try again", R.color.danger_color, R.id.fragment_cart);
                        Log.e("LoginError", "Other Error: " + response.code() + " - " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("CartFragment", "Error fetching cart: " + t.getMessage(), t);
            }
        });
    }

    private void showDialog(Context context, String userId, String productId, String productName, List<CartResponse> cartList){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_remove_item);

        TextView product_Name = dialog.findViewById(R.id.product_name);
        Button saveForLaterButton = dialog.findViewById(R.id.saveForLaterButton);
        Button removeItemButton = dialog.findViewById(R.id.removeItemButton);

        product_Name.setText(productName);

        saveForLaterButton.setOnClickListener(v -> {
            saveItem(userId, productId);
            dialog.dismiss();
        });

        removeItemButton.setOnClickListener(v -> {
            removeFromCart(userId, productId, cartList);
            dialog.dismiss();
        });

        dialog.show();
    }
}