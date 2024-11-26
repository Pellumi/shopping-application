package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobiledesignproject.ProductDisplayActivity;
import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.api.AddToCartApiService;
import com.example.mobiledesignproject.model.CartItem;
import com.example.mobiledesignproject.model.LoginResponse;
import com.example.mobiledesignproject.model.Product;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private final List<Product> productList;
    private final Context context;
    private final UIMethods ui = new UIMethods();
    Retrofit retrofit = RetrofitClient.getClient();
    AddToCartApiService cartApi = retrofit.create(AddToCartApiService.class);
    Gson gson = new Gson();

    public SearchAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_long_product, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());

        SharedPreferences shardPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String loginResponseJson = shardPreferences.getString("loginResponse", "");
        LoginResponse loginResponse = gson.fromJson(loginResponseJson, LoginResponse.class);

        String user_id = loginResponse.getUserDetails().getUserId();

        if (Integer.parseInt(product.getQuantity()) == 0){
            holder.productQty.setText("Out of stock");
            holder.itemsRemain.setVisibility(View.GONE);
        } else {
            holder.productQty.setText(product.getQuantity());
        }

        holder.productBrand.setText(product.getBrand());
        holder.productPrice.setText(ui.formatCurrency(product.getPrice()));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.color.shadow)
                .override(173, 173)
                .fitCenter()
                .into(holder.productImage);

        holder.addToCart.setOnClickListener(view -> {
//            Toast.makeText(context, "We are still working on this part", Toast.LENGTH_SHORT).show();
            addToCart(user_id, product.getProductId());
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProductDisplayActivity.class);
            intent.putExtra("Product-id", product.getProductId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQty, productBrand, itemsRemain;
        ImageView productImage;
        Button addToCart;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productQty = itemView.findViewById(R.id.product_qty);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
            itemsRemain = itemView.findViewById(R.id.items_remain);
            productBrand = itemView.findViewById(R.id.product_brand);
            addToCart = itemView.findViewById(R.id.add_to_cart);
        }
    }

    private void addToCart(String userId, String product_id){
        CartItem cartItem = new CartItem(product_id, 1);

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
