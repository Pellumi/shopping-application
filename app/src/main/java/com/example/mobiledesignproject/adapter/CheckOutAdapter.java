package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.model.CartResponse;

import java.util.List;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.CheckOutViewHolder> {

    private List<CartResponse> cartResponse;
    private Context context;

    public CheckOutAdapter(List<CartResponse> cartResponse, Context context) {
        this.cartResponse = cartResponse;
        this.context = context;
    }

    @NonNull
    @Override
    public CheckOutAdapter.CheckOutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_check_out_product, parent, false);
        return new CheckOutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutAdapter.CheckOutViewHolder holder, int position) {
        CartResponse cart = cartResponse.get(position);
        holder.productName.setText(cart.getName());
        holder.itemNumber.setText(String.valueOf(cart.getItemNumber()));
        holder.productQty.setText(String.valueOf(cart.getQuantity()));
        Glide.with(context)
                .load(cart.getImageUrl())
                .placeholder(R.color.shadow)
                .override(80, 80)
                .fitCenter()
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return cartResponse.size();
    }

    public static class CheckOutViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productQty, itemNumber;
        ImageView productImage;

        public CheckOutViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productQty = itemView.findViewById(R.id.product_quantity);
            itemNumber = itemView.findViewById(R.id.item_number);
        }
    }
}
