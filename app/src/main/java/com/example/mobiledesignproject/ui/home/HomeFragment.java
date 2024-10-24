package com.example.mobiledesignproject.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.adapter.PlaceHolderAdapter;
import com.example.mobiledesignproject.adapter.ProductAdapter;
import com.example.mobiledesignproject.adapter.SearchAdapter;
import com.example.mobiledesignproject.api.ProductApiService;
import com.example.mobiledesignproject.api.SearchByNameApiService;
import com.example.mobiledesignproject.model.Product;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ProductAdapter productAdapter;
    private SearchAdapter searchAdapter;
    EditText searchInput;
    Intent intent;
    private final UIMethods uiMethods = new UIMethods(getContext(), intent);

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // TODO: Its not also working
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (searchInput.isFocused()) {
                    searchInput.clearFocus();
                    hideKeyboard();
                }
            }
            return false;
        });

        RecyclerView fruitView = view.findViewById(R.id.fruit_list);
        RecyclerView milkView = view.findViewById(R.id.milk_list);
        RecyclerView vegetableView = view.findViewById(R.id.vegetable_list);
        RecyclerView drinkView = view.findViewById(R.id.drink_list);
        RecyclerView searchResult = view.findViewById(R.id.search_result);
        searchInput = view.findViewById(R.id.search);
        ScrollView defaultScrollView = view.findViewById(R.id.default_scrollview);
        ScrollView searchScrollView = view.findViewById(R.id.search_scrollview);
        TextView keyword = view.findViewById(R.id.keyword);
        LinearLayout resultTitle = view.findViewById(R.id.result_title);

        List<Product> fruitList = new ArrayList<>();
        List<Product> milkList = new ArrayList<>();
        List<Product> vegetableList = new ArrayList<>();
        List<Product> drinkList = new ArrayList<>();
        List<Product> searchResultList = new ArrayList<>();

        fetchProducts("101", "101-1", fruitView, fruitList);
        fetchProducts("102", "102-1", milkView, milkList);
        fetchProducts("101", "101-2", vegetableView, vegetableList);
        fetchProducts("105", "105-1", drinkView, drinkList);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchScrollView.setVisibility(View.VISIBLE);
                    defaultScrollView.setVisibility(View.GONE);
                    searchResultList.clear();
                } else {
                    searchScrollView.setVisibility(View.GONE);
                    defaultScrollView.setVisibility(View.VISIBLE);
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
                    searchByName(inputText, searchResult, searchResultList);
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

    // TODO: this part is not working too
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        searchInput = view.findViewById(R.id.search);
        searchInput.setText("");
    }

    private void fetchProducts(String categoryId, String subcategoryId, RecyclerView recyclerView, List<Product> productList){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
        PlaceHolderAdapter placeHolderAdapter = new PlaceHolderAdapter(requireActivity());
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

                            productAdapter = new ProductAdapter(productList, getContext());
                            recyclerView.setAdapter(productAdapter);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Product>> call, @NonNull Throwable t) {
                        Log.e("ProductDisplayActivity", "Error fetching products: " + t.getMessage(), t);
                    }
                });
    }

//    private void searchByDescription(String keyword, RecyclerView recyclerView, List<Product> productList){
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
//
//        recyclerView.setLayoutManager(layoutManager);
//        RetrofitClient.getClient().create(SearchApiService.class)
//                .searchByCategory(keyword)
//                .enqueue(new Callback<Map<String, Product>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<Map<String, Product>> call, @NonNull Response<Map<String, Product>> response) {
//                        if(response.isSuccessful() && response.body() != null){
//                            Map<String, Product> productMap = response.body();
//                            for(Map.Entry<String, Product> entry : productMap.entrySet()){
//                                Product product = entry.getValue();
//                                productList.add(product);
//                            }
//
//                            searchAdapter = new SearchAdapter(productList, requireActivity());
//                            recyclerView.setAdapter(searchAdapter);
//                            recyclerView.setVisibility(View.VISIBLE);
//                        } else {
//                            if (response.code() == 404){
//                                uiMethods.showRegularSnackBar(requireContext(), "No products found in this category", R.color.danger_color, R.id.fragment_home);
//                            } else if (response.code() == 400) {
//                                Toast.makeText(getContext(), "Enter a search query", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getContext(), "Login failed, please try again later " + response.message(), Toast.LENGTH_SHORT).show();
//                                Log.e("LoginError", "Other Error: " + response.code() + " - " + response.message());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<Map<String, Product>> call, @NonNull Throwable t) {
//                        Log.e("ProductDisplayActivity", "Error fetching products: " + t.getMessage(), t);
//                    }
//                });
//    }

    private void searchByName(String keyword, RecyclerView recyclerView, List<Product> productList){

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        RetrofitClient.getClient().create(SearchByNameApiService.class)
                .searchByName(keyword)
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
                                uiMethods.showRegularSnackBar(requireContext(), "No products found with this  name", R.color.danger_color, R.id.fragment_home);
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
                        Log.e("HomeFragment", "Error fetching products: " + t.getMessage(), t);
                    }
                });
    }

    // TODO: Its not working
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        }
    }
}