package com.example.mobiledesignproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.api.ProductApiService;
import com.example.mobiledesignproject.model.Product;
import com.example.mobiledesignproject.model.SubCategory;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder> {
    private List<SubCategory> subcategories = new ArrayList<>();
    private Context context;
    private CategoryItemAdapter categoryItemAdapter;
    private final UIMethods ui = new UIMethods();

    public SubCategoryAdapter(Context context) {
        this.context = context;
    }

    public void setSubcategories(List<SubCategory> subcategories) {
        this.subcategories = subcategories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubCategoryAdapter.SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_category_tab, parent, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryAdapter.SubCategoryViewHolder holder, int position) {
        SubCategory sub = subcategories.get(position);
        String categoryId = ui.getFirstThreeChars(sub.getId());
        String subCategoryId = sub.getId();

        List<Product> productList = new ArrayList<>();

        holder.subCategoryName.setText(ui.capitalizeWord(sub.getName()));
        fetchProducts(categoryId, subCategoryId, holder.productList, productList, holder.emptyProduct);
    }

    @Override
    public int getItemCount() {
        return subcategories.size();
    }

    public static class SubCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView subCategoryName;
        RecyclerView productList;
        LinearLayout emptyProduct;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            subCategoryName = itemView.findViewById(R.id.sub_category_name);
            productList = itemView.findViewById(R.id.product_list);
            emptyProduct = itemView.findViewById(R.id.empty_product_list);
        }
    }

    private void fetchProducts(String categoryId, String subcategoryId, RecyclerView recyclerView, List<Product> productList, LinearLayout linearLayout){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
        productList.clear();

        recyclerView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        PlaceHolderAdapter placeHolderAdapter = new PlaceHolderAdapter(context, R.layout.component_category_item_placeholder);
        recyclerView.setAdapter(placeHolderAdapter);
        RetrofitClient.getClient().create(ProductApiService.class)
                .getProducts(categoryId, subcategoryId)
                .enqueue(new Callback<Map<String, Product>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Product>> call, @NonNull Response<Map<String, Product>> response) {
                        if(response.isSuccessful() && response.body() != null){
                            Map<String, Product> productMap = response.body();
                            for(Map.Entry<String, Product> entry : productMap.entrySet()){
                                Product product = entry.getValue();
                                productList.add(product);
                            }

                            categoryItemAdapter = new CategoryItemAdapter(productList, context);
                            recyclerView.setAdapter(categoryItemAdapter);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Product>> call, @NonNull Throwable t) {
                        Log.e("ProductDisplayActivity", "Error fetching products: " + t.getMessage(), t);
                    }
                });
    }
}
