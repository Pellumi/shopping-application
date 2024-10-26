package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.model.Category;
import com.example.mobiledesignproject.model.SubCategory;
import com.example.mobiledesignproject.ui.UIMethods;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> category;
    private Context context;
    private UIMethods ui = new UIMethods();
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    public CategoryAdapter(List<Category> category, Context context, OnCategoryClickListener listener) {
        this.category = category;
        this.context = context;
        this.listener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(List<SubCategory> subcategories);
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        Category cat = category.get(position);
        holder.categoryName.setText(ui.capitalizeWord(cat.getName()));

        if (position == selectedPosition) {
            holder.borderView.setVisibility(View.VISIBLE);
        } else {
            holder.borderView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            listener.onCategoryClick(cat.getSubCategories());
        });
    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        View borderView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.category_name);
            borderView = itemView.findViewById(R.id.bd_view);
        }
    }
}
