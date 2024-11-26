package com.example.mobiledesignproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.mobiledesignproject.api.AddToCartApiService;
import com.example.mobiledesignproject.api.SingleProductApiService;
import com.example.mobiledesignproject.model.CartItem;
import com.example.mobiledesignproject.model.LoginResponse;
import com.example.mobiledesignproject.model.Product;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductDisplayActivity extends AppCompatActivity {

    ImageView cartIcon, productImage;
    TextView productName, productPrice, productId, productBrand, quantity, productQty;
    private int qty = 1;
    Button addToCart, backToMain, btnDecrease, btnIncrease, backToHome;
    Intent intent;
    List<Product> productList;
    Context context = ProductDisplayActivity.this;
    Retrofit retrofit = RetrofitClient.getClient();
    AddToCartApiService cartApi = retrofit.create(AddToCartApiService.class);
    private final UIMethods ui = new UIMethods(context, intent);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_display);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColor));

        intent = getIntent();
        String product_id = intent.getStringExtra("Product-id");

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String loginResponseJson = sharedPreferences.getString("loginResponse", "");
        LoginResponse loginResponse = gson.fromJson(loginResponseJson, LoginResponse.class);

        String userId = loginResponse.getUserDetails().getUserId();

        productQty = findViewById(R.id.product_qty);
        quantity = findViewById(R.id.item_qty);
        backToMain = findViewById(R.id.back_arrow);
        cartIcon = findViewById(R.id.shop_cart);
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productId = findViewById(R.id.product_id);
        productBrand = findViewById(R.id.product_brand);
        addToCart = findViewById(R.id.add_to_cart);
        btnDecrease = findViewById(R.id.btn_decrease);
        btnIncrease = findViewById(R.id.btn_increase);
        backToHome = findViewById(R.id.back_to_home);
        productList = new ArrayList<>();

        backToMain.setOnClickListener(v -> {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        cartIcon.setOnClickListener(v -> {
            intent = new Intent(this, MainActivity.class);
            intent.setData(Uri.parse("myapp://navigation_cart"));
            startActivity(intent);
        });

        btnIncrease.setOnClickListener(v -> {
            qty++;
            productQty.setText(String.valueOf(qty));
        });

        btnDecrease.setOnClickListener(v -> {
            if (qty > 1) {
                qty--;
                productQty.setText(String.valueOf(qty));
            } else {
                ui.showLowerRegularSnackBar(context, "Quantity cannot be less than 1", R.color.danger_color, R.id.main);
            }
        });


        if(product_id != null){
            fetchProduct(product_id, productName, productPrice, productId, productBrand, productImage, quantity);
        } else {
            Toast.makeText(this, "No item ID provided", Toast.LENGTH_SHORT).show();
            intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        backToHome.setOnClickListener(v -> {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        addToCart.setOnClickListener(v -> {
            String qtyString = productQty.getText().toString().trim();
            int quantity = Integer.parseInt(qtyString);
            addToCart(userId, product_id, quantity);
        });
    }

    private void fetchProduct(String product_id, TextView productName, TextView productPrice, TextView productId, TextView productBrand, ImageView productImage, TextView quantity){
        Log.e("ProductDisplayActivity", "Started fetching products");
        RetrofitClient.getClient().create(SingleProductApiService.class)
                .getProduct(product_id)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                        if(response.isSuccessful() && response.body() != null){
                            Log.e("ProductDisplayActivity", "response successful, assigning ui");
                            Product product = response.body();
                            productName.setText(product.getName());
                            quantity.setText(String.valueOf(product.getQuantity()));
                            productPrice.setText(ui.formatCurrency(product.getPrice()));
                            productId.setText(product_id);
                            productBrand.setText(product.getBrand());
                            Glide.with(context)
                                    .load(product.getImageUrl())
                                    .placeholder(R.color.shadow)
                                    .fitCenter()
                                    .into(productImage);
                            Log.e("ProductDisplayActivity", "Ui assigned");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                        Log.e("ProductDisplayActivity", "Error fetching products: " + t.getMessage(), t);
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
}