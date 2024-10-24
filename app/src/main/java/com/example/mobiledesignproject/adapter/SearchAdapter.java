package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobiledesignproject.ProductDisplayActivity;
import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.ui.UIMethods;
import com.example.mobiledesignproject.model.Product;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private final List<Product> productList;
    private final Context context;
    private final UIMethods uiMethods = new UIMethods();

    public SearchAdapter(List<Product> productList, Context context) {
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
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_long_product, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        if (Integer.parseInt(product.getQuantity()) == 0){
            holder.productQty.setText("Out of stock");
            holder.itemsRemain.setVisibility(View.GONE);
        } else {
            holder.productQty.setText(product.getQuantity());
        }
        holder.productBrand.setText(product.getBrand());
        holder.productPrice.setText(uiMethods.formatCurrency(product.getPrice()));
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.color.shadow)
                .override(173, 173)
                .fitCenter()
                .into(holder.productImage);
        holder.addToCart.setOnClickListener(view -> {
            Toast.makeText(context, "We are still working on this part", Toast.LENGTH_SHORT).show();
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
}
