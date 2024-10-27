package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceHolderAdapter extends RecyclerView.Adapter<PlaceHolderAdapter.PlaceHolderViewHolder> {

    private final Context context;
    private final int id;

    public PlaceHolderAdapter(Context context, int id) {
        this.context = context;
        this.id = id;
    }

    @NonNull
    @Override
    public PlaceHolderAdapter.PlaceHolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(id, parent, false);
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
