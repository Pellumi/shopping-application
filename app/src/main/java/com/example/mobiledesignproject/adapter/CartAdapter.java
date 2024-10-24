package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.ui.UIMethods;
import com.example.mobiledesignproject.model.CartResponse;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartResponse> cartResponse;
    private Context context;
    private OnDialogOpenListener onDialogOpenListener;
    private UIMethods uiMethods = new UIMethods();

    public CartAdapter(List<CartResponse> cartResponse, Context context, OnDialogOpenListener onDialogOpenListener) {
        this.cartResponse = cartResponse;
        this.context = context;
        this.onDialogOpenListener = onDialogOpenListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartResponse cart = cartResponse.get(position);
        holder.productName.setText(cart.getName());
        holder.productQty.setText(String.valueOf(cart.getQuantity()));
        holder.product_qty.setText(String.valueOf(cart.getQuantity()));
        holder.productPrice.setText(uiMethods.formatCurrency(String.valueOf(cart.getPrice())));
        holder.totalPrice.setText(uiMethods.formatCurrency(String.valueOf(cart.getTotalPrice())));
        Glide.with(context)
                .load(cart.getImageUrl())
                .placeholder(R.color.shadow)
                .override(150, 150)
                .fitCenter()
                .into(holder.productImage);
        holder.btnSaveForLater.setOnClickListener(v -> {
            onDialogOpenListener.onDialogOpen(cart);
        });
        holder.btnRemoveItem.setOnClickListener(v -> {
            onDialogOpenListener.onDialogOpen(cart);
        });
    }

    @Override
    public int getItemCount() {
        return cartResponse.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQty, product_qty, totalPrice;
        ImageView productImage;
        Button btnSaveForLater, btnRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productQty = itemView.findViewById(R.id.productQuantity);
            product_qty = itemView.findViewById(R.id.product_quantity);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            btnSaveForLater = itemView.findViewById(R.id.btn_save_for_later);
            btnRemoveItem = itemView.findViewById(R.id.btn_remove_item);
        }
    }

    public interface OnDialogOpenListener{
        void onDialogOpen(CartResponse cart);
    }
}
