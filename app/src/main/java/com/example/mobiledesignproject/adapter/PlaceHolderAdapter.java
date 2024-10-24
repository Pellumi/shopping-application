package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.R;

public class PlaceHolderAdapter extends RecyclerView.Adapter<PlaceHolderAdapter.PlaceHolderViewHolder> {

    private final Context context;

    public PlaceHolderAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PlaceHolderAdapter.PlaceHolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_main_placeholder, parent, false);
        return new PlaceHolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolderAdapter.PlaceHolderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class PlaceHolderViewHolder extends RecyclerView.ViewHolder {
        public PlaceHolderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
