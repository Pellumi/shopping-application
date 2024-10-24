package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.R;

public class CheckOutPlaceHolderAdapter extends RecyclerView.Adapter<CheckOutPlaceHolderAdapter.CheckOutPlaceHolderViewHolder> {

    private final Context context;

    public CheckOutPlaceHolderAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CheckOutPlaceHolderAdapter.CheckOutPlaceHolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_check_out_placeholder, parent, false);
        return new CheckOutPlaceHolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutPlaceHolderAdapter.CheckOutPlaceHolderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class CheckOutPlaceHolderViewHolder extends RecyclerView.ViewHolder {
        public CheckOutPlaceHolderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
