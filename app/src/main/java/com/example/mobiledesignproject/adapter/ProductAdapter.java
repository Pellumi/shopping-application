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
import com.example.mobiledesignproject.ui.UIMethods;
import com.example.mobiledesignproject.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final Context context;
    private final UIMethods uiMethods = new UIMethods();

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

//    public static String formatCurrency(String numberString){
//        if (numberString.isEmpty()) {
//            return numberString;
//        }
//
//        String[] parts = numberString.split("\\.");
//        String integerPart = parts[0];
//
//        StringBuilder reversedInteger = new StringBuilder(integerPart).reverse();
//
//        StringBuilder formattedInteger = new StringBuilder();
//        for (int i = 0; i < reversedInteger.length(); i++) {
//            formattedInteger.append(reversedInteger.charAt(i));
//            if (i != 0 && (i + 1) % 3 == 0 && reversedInteger.length() > 3) {
//                formattedInteger.append(",");
//            }
//        }
//
//        String formattedNumber = formattedInteger.reverse().toString();
//
//        if (parts.length > 1) {
//            formattedNumber += "." + parts[1];
//        }
//
//        return "₦" + formattedNumber;
//    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_main_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        if (Integer.parseInt(product.getQuantity()) == 0){
            holder.productQty.setText("Out of stock");
            holder.productQty.setTextSize(12);
            holder.itemsRemain.setText("           ");
        } else {
            holder.productQty.setText(product.getQuantity());
        }
        holder.productPrice.setText(uiMethods.formatCurrency(product.getPrice()));
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.color.shadow)
                .override(100, 100)
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

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQty, itemsRemain;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productQty = itemView.findViewById(R.id.product_qty);
            productPrice = itemView.findViewById(R.id.product_price);
            itemsRemain = itemView.findViewById(R.id.items_remain);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
