package com.example.mobiledesignproject.ui.category;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.adapter.CategoryAdapter;
import com.example.mobiledesignproject.adapter.CategoryPlaceHolderAdapter;
import com.example.mobiledesignproject.adapter.PlaceHolderAdapter;
import com.example.mobiledesignproject.adapter.SearchAdapter;
import com.example.mobiledesignproject.adapter.SubCategoryAdapter;
import com.example.mobiledesignproject.api.FetchCategoryApiService;
import com.example.mobiledesignproject.api.SearchApiService;
import com.example.mobiledesignproject.model.Category;
import com.example.mobiledesignproject.model.Product;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {
    private RecyclerView categoryView, subCategoryView;
    private CategoryAdapter categoryAdapter;
    private SubCategoryAdapter subCategoryAdapter;
    EditText searchInput;
    private final UIMethods uiMethods = new UIMethods();
    private SearchAdapter searchAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        categoryView = view.findViewById(R.id.category_list);
        subCategoryView = view.findViewById(R.id.sub_category_list);
        RecyclerView searchResult = view.findViewById(R.id.search_result);
        searchInput = view.findViewById(R.id.search);
        LinearLayout defaultLayout = view.findViewById(R.id.default_layout);
        ScrollView searchScrollView = view.findViewById(R.id.search_scrollview);
        TextView keyword = view.findViewById(R.id.keyword);
        LinearLayout resultTitle = view.findViewById(R.id.result_title);

        List<Category> categoryList = new ArrayList<>();
        List<Product> searchResultList = new ArrayList<>();

        fetchCategories(categoryView, subCategoryView, categoryList);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchScrollView.setVisibility(View.VISIBLE);
                    defaultLayout.setVisibility(View.GONE);
                    searchResultList.clear();
                } else {
                    searchScrollView.setVisibility(View.GONE);
                    defaultLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchInput.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {

                String inputText = searchInput.getText().toString().trim();
                if (!inputText.isEmpty() && inputText.length() >= 3) {
                    keyword.setText(inputText);
                    resultTitle.setVisibility(View.VISIBLE);
                    searchByDescription(inputText, searchResult, searchResultList);
                } else {
                    uiMethods.showRegularSnackBar(requireContext(), "Input text cannot be less than 3 characters", R.color.danger_color, R.id.fragment_home);
                }

                searchInput.clearFocus();
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);

                return true;
            }
            return false;
        });
        return view;
    }

    private void fetchCategories(RecyclerView categoryRecyclerView, RecyclerView subCategoryRecyclerView, List<Category> categoryList) {
        Log.e("CategoryFragment", "Started fetching categories");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager);

        CategoryPlaceHolderAdapter categoryPlaceHolderAdapter = new CategoryPlaceHolderAdapter();
        categoryRecyclerView.setAdapter(categoryPlaceHolderAdapter);

        subCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PlaceHolderAdapter subCategoryPlaceHolderAdapter = new PlaceHolderAdapter(getContext(), R.layout.component_category_tab_placeholder);
        subCategoryRecyclerView.setAdapter(subCategoryPlaceHolderAdapter);

        RetrofitClient.getClient().create(FetchCategoryApiService.class)
                .fetchCategory()
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("CategoryFragment", "response successful, assigning UI");

                            categoryList.addAll(response.body());

                            categoryAdapter = new CategoryAdapter(categoryList, getContext(), subcategories -> {
                                subCategoryAdapter.setSubcategories(subcategories);
                            });
                            categoryRecyclerView.setAdapter(categoryAdapter);

                            subCategoryAdapter = new SubCategoryAdapter(getContext());
                            subCategoryRecyclerView.setAdapter(subCategoryAdapter);

                            if(!categoryList.isEmpty()){
                                Category firstCategory = categoryList.get(0);
                                subCategoryAdapter.setSubcategories(firstCategory.getSubCategories());
                            }
                            Log.e("CategoryFragment", "Ui attached");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                        Log.e("CategoryFragment", "Error fetching products: " + t.getMessage(), t);
                    }
                });
    }

    private void searchByDescription(String keyword, RecyclerView recyclerView, List<Product> productList){

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        RetrofitClient.getClient().create(SearchApiService.class)
                .searchByCategory(keyword)
                .enqueue(new Callback<Map<String, Product>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Product>> call, @NonNull Response<Map<String, Product>> response) {
                        if(response.isSuccessful() && response.body() != null){
                            Map<String, Product> productMap = response.body();
                            for(Map.Entry<String, Product> entry : productMap.entrySet()){
                                Product product = entry.getValue();
                                productList.add(product);
                            }

                            searchAdapter = new SearchAdapter(productList, requireActivity());
                            recyclerView.setAdapter(searchAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            if (response.code() == 404){
                                uiMethods.showRegularSnackBar(requireContext(), "No products found in this category", R.color.danger_color, R.id.fragment_home);
                            } else if (response.code() == 400) {
                                Toast.makeText(getContext(), "Enter a search query", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Login failed, please try again later " + response.message(), Toast.LENGTH_SHORT).show();
                                Log.e("LoginError", "Other Error: " + response.code() + " - " + response.message());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Product>> call, @NonNull Throwable t) {
                        Log.e("ProductDisplayActivity", "Error fetching products: " + t.getMessage(), t);
                    }
                });
    }
}