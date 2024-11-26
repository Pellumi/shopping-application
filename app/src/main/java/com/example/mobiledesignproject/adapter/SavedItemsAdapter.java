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
import com.example.mobiledesignproject.model.SavedItemsResponse;
import com.example.mobiledesignproject.ui.UIMethods;

import java.util.List;

public class SavedItemsAdapter extends RecyclerView.Adapter<SavedItemsAdapter.ViewHolder> {

    private final List<SavedItemsResponse.SavedItem> savedItemList;
    private final Context context;
    private OnRemoveItemDialogOpenListener onRemoveItemDialogOpenListener;
    private OnAddToCartDialogOpenListener onAddToCartDialogOpenListener;
    private final UIMethods uiMethods = new UIMethods();

    public SavedItemsAdapter(List<SavedItemsResponse.SavedItem> savedItemList, Context context, OnRemoveItemDialogOpenListener onRemoveItemDialogOpenListener, OnAddToCartDialogOpenListener onAddToCartDialogOpenListener) {
        this.savedItemList = savedItemList;
        this.context = context;
        this.onRemoveItemDialogOpenListener = onRemoveItemDialogOpenListener;
        this.onAddToCartDialogOpenListener = onAddToCartDialogOpenListener;
    }

    @NonNull
    @Override
    public SavedItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_saved_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedItemsAdapter.ViewHolder holder, int position) {
        SavedItemsResponse.SavedItem item = savedItemList.get(position);
        holder.productName.setText(item.getName());
        holder.productAmount.setText(String.valueOf(item.getQuantity()));
        holder.productPrice.setText(uiMethods.formatCurrency(String.valueOf(item.getPrice())));
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.color.shadow)
                .override(150, 150)
                .fitCenter()
                .into(holder.productImage);
        holder.btnAddToCart.setOnClickListener(v -> {
//            Toast.makeText(context, "We are still working on this", Toast.LENGTH_SHORT).show();
            onAddToCartDialogOpenListener.onDialogOpen(item);
        });
        holder.btnRemoveItem.setOnClickListener(v -> {
//            Toast.makeText(context, "We are still working on this", Toast.LENGTH_SHORT).show();
            onRemoveItemDialogOpenListener.onDialogOpen(item);
        });
    }

    @Override
    public int getItemCount() {
        return savedItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName, productPrice, productBrand, btnRemoveItem, productAmount;
        ImageView productImage;
        Button btnAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productBrand = itemView.findViewById(R.id.product_brand);
            productPrice = itemView.findViewById(R.id.product_price);
            productAmount = itemView.findViewById(R.id.product_amount);
            productImage = itemView.findViewById(R.id.product_image);
            btnAddToCart = itemView.findViewById(R.id.add_to_cart);
            btnRemoveItem = itemView.findViewById(R.id.remove_item);
        }
    }

    public interface OnRemoveItemDialogOpenListener{
        void onDialogOpen(SavedItemsResponse.SavedItem item);
    }

    public interface OnAddToCartDialogOpenListener{
        void onDialogOpen(SavedItemsResponse.SavedItem item);
    }
}
