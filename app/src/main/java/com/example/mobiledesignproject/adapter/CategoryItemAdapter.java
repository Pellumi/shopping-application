package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobiledesignproject.ProductDisplayActivity;
import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.model.Product;

import java.util.List;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.CategoryItemViewHolder> {

    private List<Product> productList;
    private Context context;

    public CategoryItemAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryItemAdapter.CategoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_category_item, parent, false);
        return new CategoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryItemAdapter.CategoryItemViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.color.shadow)
                .override(80, 80)
                .fitCenter()
                .into(holder.productImage);
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

    public static class CategoryItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;

        public CategoryItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
        }
    }
}
