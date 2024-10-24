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
import com.example.mobiledesignproject.ui.UIMethods;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> category;
    private Context context;
    private UIMethods ui = new UIMethods();

    public CategoryAdapter(List<Category> category, Context context) {
        this.category = category;
        this.context = context;
    }

//    public String capitalizeWord(String input) {
//        if (input == null || input.isEmpty()) {
//            return input;
//        }
//
//        StringBuilder result = new StringBuilder();
//        String[] words = input.split("\\s+");
//
//        for (String word : words) {
//            if (word.length() > 1) {
//                result.append(Character.toUpperCase(word.charAt(0)))
//                        .append(word.substring(1).toLowerCase());
//            } else {
//                result.append(word.toUpperCase());
//            }
//            result.append(" ");
//        }
//
//        return result.toString().trim();
//    }

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
    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}
