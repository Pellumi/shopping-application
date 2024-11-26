package com.example.mobiledesignproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.model.Order;
import com.example.mobiledesignproject.ui.UIMethods;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final List<Order> orderList;
    private final UIMethods ui = new UIMethods();
    private OnOrderClickListener listener;

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderDate.setText(ui.convertDate(order.getOrderDate()));
        holder.orderAddress.setText(order.getAddress());
        holder.orderName.setText(ui.capitalizeWord(order.getName()));
        holder.orderAmount.setText(ui.formatCurrency(order.getTotalAmount()));
        holder.orderStatus.setText(ui.capitalizeWord(order.getStatus()));
        holder.orderQty.setText(String.valueOf(order.getItems().size()) + " Item(s)");

        holder.itemView.setOnClickListener(v -> {
            listener.onOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate, orderAddress, orderName, orderAmount, orderStatus, orderQty;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderDate = itemView.findViewById(R.id.order_date);
            orderAddress = itemView.findViewById(R.id.order_address);
            orderName = itemView.findViewById(R.id.order_name);
            orderAmount = itemView.findViewById(R.id.order_amount);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderQty = itemView.findViewById(R.id.order_qty);
        }
    }
}
