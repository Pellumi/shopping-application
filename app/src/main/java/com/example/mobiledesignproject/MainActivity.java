package com.example.mobiledesignproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mobiledesignproject.databinding.ActivityMainBinding;
import com.example.mobiledesignproject.network.NetworkUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            View fragmentHolder = findViewById(R.id.nav_host_fragment_activity_main);
            View noInternetLayout = findViewById(R.id.no_internet_layout);
            BottomNavigationView navView = findViewById(R.id.nav_view);

            ImageView cartIcon = findViewById(R.id.cart_icon);
            cartIcon.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setData(Uri.parse("myapp://navigation_cart"));
                startActivity(intent);
            });

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupWithNavController(navView, navController);

            if (NetworkUtils.isNetworkConnected(this)) {
                showMainContent(fragmentHolder, noInternetLayout);
            } else {
                showNoInternetScreen(fragmentHolder, noInternetLayout);
            }
        }
    }

    private void showMainContent(View fragmentHolder, View noInternetLayout) {
        fragmentHolder.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
    }

    private void showNoInternetScreen(View fragmentHolder, View noInternetLayout) {
        fragmentHolder.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }
}