package com.example.mobiledesignproject.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.model.Item;
import com.example.mobiledesignproject.ui.UIMethods;

import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {
    private List<Item> items;
    private UIMethods ui = new UIMethods();

    public OrderItemsAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_order_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemsAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.priceTextView.setText(ui.formatCurrencyNoSymbol(item.getPrice()));
        holder.nameTextView.setText(item.getName());
        holder.quantityTextView.setText(ui.formatToTwoDigits(String.valueOf(item.getQuantity())));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView priceTextView, nameTextView, quantityTextView;

        ViewHolder(View itemView) {
            super(itemView);
            priceTextView = itemView.findViewById(R.id.item_price);
            nameTextView = itemView.findViewById(R.id.item_name);
            quantityTextView = itemView.findViewById(R.id.order_qty);
        }
    }
}

