package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.R;

public class CartPlaceHolderAdapter extends RecyclerView.Adapter<CartPlaceHolderAdapter.CartPlaceHolderViewHolder> {

    private final Context context;

    public CartPlaceHolderAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CartPlaceHolderAdapter.CartPlaceHolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_cart_placeholder, parent, false);
        return new CartPlaceHolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartPlaceHolderAdapter.CartPlaceHolderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class CartPlaceHolderViewHolder extends RecyclerView.ViewHolder {
        public CartPlaceHolderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
