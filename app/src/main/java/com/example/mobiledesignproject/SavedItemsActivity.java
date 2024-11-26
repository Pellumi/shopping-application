package com.example.mobiledesignproject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.adapter.PlaceHolderAdapter;
import com.example.mobiledesignproject.adapter.SavedItemsAdapter;
import com.example.mobiledesignproject.api.AddToCartApiService;
import com.example.mobiledesignproject.api.GetSavedItemApiClass;
import com.example.mobiledesignproject.api.RemoveSavedItemApiService;
import com.example.mobiledesignproject.api.SingleProductApiService;
import com.example.mobiledesignproject.model.CartItem;
import com.example.mobiledesignproject.model.LoginResponse;
import com.example.mobiledesignproject.model.Product;
import com.example.mobiledesignproject.model.SavedItemsResponse;
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

public class SavedItemsActivity extends AppCompatActivity {

    ImageView backArrow;
    Intent intent;
    private Context context = SavedItemsActivity.this;
    private SharedPreferences shardPreferences;
    Gson gson = new Gson();
    RecyclerView savedItemsView;
    private UIMethods ui = new UIMethods();
    Retrofit retrofit = RetrofitClient.getClient();
    AddToCartApiService cartApi = retrofit.create(AddToCartApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_items);
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
        
        backArrow = findViewById(R.id.back_arrow);
        savedItemsView = findViewById(R.id.saved_items_view);
        List<SavedItemsResponse.SavedItem> savedItemList = new ArrayList<>();

        backArrow.setOnClickListener(v -> {
            intent = new Intent(context, MainActivity.class);
            intent.setData(Uri.parse("myapp://navigation_account"));
            startActivity(intent);
        });

        fetchSavedItems(user_id, savedItemsView, savedItemList);
    }

    private void fetchSavedItems(String userId, RecyclerView recyclerView, List<SavedItemsResponse.SavedItem> savedItemList){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        PlaceHolderAdapter savedItemPlaceholder = new PlaceHolderAdapter(context, R.layout.component_saved_items_placeholder);
        recyclerView.setAdapter(savedItemPlaceholder);

        RetrofitClient.getClient().create(GetSavedItemApiClass.class)
                .getSavedItems(userId)
                .enqueue(new Callback<SavedItemsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SavedItemsResponse> call, @NonNull Response<SavedItemsResponse> response) {
                        if(response.isSuccessful() && response.body() != null){
                            SavedItemsResponse savedItemsResponse = response.body();
                            Map<String, SavedItemsResponse.SavedItem> savedItemMap = savedItemsResponse.getSavedItems();

                            List<SavedItemsResponse.SavedItem> tempList = new ArrayList<>();
                            CountDownLatch latch = new CountDownLatch(savedItemMap.size());

                            for(Map.Entry<String, SavedItemsResponse.SavedItem> entry : savedItemMap.entrySet()){
                                SavedItemsResponse.SavedItem savedItem = entry.getValue();
                                String productId = savedItem.getProductId();

                                RetrofitClient.getClient().create(SingleProductApiService.class)
                                        .getProduct(productId)
                                        .enqueue(new Callback<Product>() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                                                if(response.isSuccessful() && response.body() != null){
                                                    Product product = response.body();
                                                    savedItem.setName(product.getName());
                                                    savedItem.setBrand(product.getBrand());
                                                    savedItem.setImageUrl(product.getImageUrl());
                                                    savedItem.setPrice(product.getPrice());
                                                    savedItem.setQuantity(product.getQuantity());

                                                    tempList.add(savedItem);
                                                }
                                                latch.countDown();
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                                                Log.e("SavedItemActivity", "Error fetching product details: " + t.getMessage(), t);
                                                latch.countDown();
                                            }
                                        });
                            }

                            new Thread(() -> {
                                try{
                                    latch.await();
                                    runOnUiThread(() -> {
                                        savedItemList.addAll(tempList);
                                        SavedItemsAdapter savedItemsAdapter = new SavedItemsAdapter(savedItemList, context, item -> {showRemoveDialog(context, userId, item.getProductId(), savedItemList);}, item -> {showAddToCartDialog(context, userId, item.getProductId());});
                                        recyclerView.setAdapter(savedItemsAdapter);
                                    });
                                } catch (Exception e) {
                                    Log.e("SavedItemsActivity", "Interrupted while waiting for product details", e);
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SavedItemsResponse> call, @NonNull Throwable t) {
                        Log.e("SavedItemsActivity", "Error fetching items: " + t.getMessage(), t);
                    }
                });
    }

    private void addToCart(String userId, String product_id, int quantity){
        CartItem cartItem = new CartItem(product_id, quantity);

        Call<Void> call = cartApi.addToCart(userId, cartItem);
        call.enqueue(new retrofit2.Callback<Void>(){

            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()){
                    if(response.message().equals("Product quantity updated in cart")){
                        ui.showActionSnackBar(context, "Item quantity updated in cart", "Cart", R.color.btn_acc);
                    } else {
                        ui.showActionSnackBar(context,"Item quantity updated in cart", "Cart", R.color.btn_acc);
                        Log.e("AddToCart", "Cart: " + response.body());
                    }
                } else{
                    if (response.code() == 404){
                        ui.showLowerRegularSnackBar(context, "Failed to add item, Please try again", R.color.danger_color, R.id.main);
                        Log.e("AddToCart", "Error: " + response.message());
                    } else if (response.code() == 400) {
                        ui.showLowerRegularSnackBar(context, "Invalid Quantity", R.color.danger_color, R.id.main);
                    } else {
                        ui.showLowerRegularSnackBar(context, "Failed to add item, Please try again", R.color.danger_color, R.id.main);
                        Log.e("AddToCart", "Other Error: " + response.code() + " - " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("AddToCart", "Error: " + t.getMessage());
            }
        });
    }

    private void removeSavedItems(String userId, String productId, List<SavedItemsResponse.SavedItem> savedItemList){
        Retrofit retrofit = RetrofitClient.getClient();
        RemoveSavedItemApiService removeSavedItemService = retrofit.create(RemoveSavedItemApiService.class);

        Call<Void> call = removeSavedItemService.removedSavedItem(userId, productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    savedItemList.clear();
                    fetchSavedItems(userId, savedItemsView, savedItemList);
                    ui.showLowerRegularSnackBar(context, "Item successfully removed from wishlist", R.color.btn_acc, R.id.main);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("SavedItemsActivity", "Error removing saved item: " + t.getMessage(), t);
                ui.showLowerRegularSnackBar(context, "Failed to remove item, please try again later", R.color.danger_color, R.id.main);
            }
        });
    }

    private void showRemoveDialog(Context context, String userId, String productId, List<SavedItemsResponse.SavedItem> savedItemList){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_order);

        Button cancel = dialog.findViewById(R.id.cancel_order);
        Button removeItem = dialog.findViewById(R.id.confirm_order);
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogText = dialog.findViewById(R.id.dialog_message);

        dialogTitle.setText("Remove from Saved Items");
        dialogText.setText("Clicking confirm will remove this item from your wishlist");

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        removeItem.setOnClickListener(v -> {
            removeSavedItems(userId, productId, savedItemList);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showAddToCartDialog(Context context, String userId, String productId){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_order);

        Button cancel = dialog.findViewById(R.id.cancel_order);
        Button addToCartBtn = dialog.findViewById(R.id.confirm_order);
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogText = dialog.findViewById(R.id.dialog_message);

        dialogTitle.setText("Add item to Cart");
        dialogText.setText("Clicking confirm will add this item from your cart");

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        addToCartBtn.setOnClickListener(v -> {
            addToCart(userId, productId, 1);
            dialog.dismiss();
        });

        dialog.show();
    }
}