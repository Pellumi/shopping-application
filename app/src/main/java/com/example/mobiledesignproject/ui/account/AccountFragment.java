package com.example.mobiledesignproject.ui.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobiledesignproject.LoginActivity;
import com.example.mobiledesignproject.OrderedItemsActivity;
import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.model.LoginResponse;
import com.example.mobiledesignproject.ui.UIMethods;
import com.google.gson.Gson;

public class AccountFragment extends Fragment {

    TextView userName, userId, mEmail, mLogOut;
    Intent intent;
    SharedPreferences shardPreferences;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();
    Button orderNav, inboxNav, savedNav, rateNav, recentSearchNav;
    private UIMethods uiMethods = new UIMethods();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        userName = view.findViewById(R.id.user_name);
        userId = view.findViewById(R.id.user_id);
        mEmail = view.findViewById(R.id.user_email);
        mLogOut = view.findViewById(R.id.log_out);
        orderNav = view.findViewById(R.id.nav_to_order);
        inboxNav = view.findViewById(R.id.nav_to_inbox);
        savedNav = view.findViewById(R.id.nav_to_saved);
        rateNav = view.findViewById(R.id.nav_to_rate);
        recentSearchNav = view.findViewById(R.id.nav_to_recent_search);

        shardPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String loginResponseJson = shardPreferences.getString("loginResponse", "");
        LoginResponse loginResponse = gson.fromJson(loginResponseJson, LoginResponse.class);

        String user_id = loginResponse.getUserDetails().getUserId();
        String first_name = loginResponse.getUserDetails().getFirstName();
        String last_name = loginResponse.getUserDetails().getLastName();
        String email = loginResponse.getUserDetails().getEmail();

        userName.setText(first_name + " " + last_name);
        userId.setText(uiMethods.replaceHyphenWithSlash(user_id));
        mEmail.setText(email);

        orderNav.setOnClickListener(v -> {
            intent = new Intent(getContext(), OrderedItemsActivity.class);
            startActivity(intent);
        });

        inboxNav.setOnClickListener(v -> {
            Toast.makeText(getContext(), "This will be available in the next update", Toast.LENGTH_SHORT).show();
        });

        savedNav.setOnClickListener(v -> {
//            intent = new Intent(getContext(), SavedItemsActivity.class);
//            startActivity(intent);
            Toast.makeText(getContext(), "This will be available in the next update", Toast.LENGTH_SHORT).show();
        });

        rateNav.setOnClickListener(v -> {
            Toast.makeText(getContext(), "This will be available in the next update", Toast.LENGTH_SHORT).show();
        });

        recentSearchNav.setOnClickListener(v -> {
//            intent = new Intent(getContext(), RecentSearchActivity.class);
//            startActivity(intent);
            Toast.makeText(getContext(), "This will be available in the next update", Toast.LENGTH_SHORT).show();
        });
        
        mLogOut.setOnClickListener(v -> {
            shardPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            editor = shardPreferences.edit();
            editor.clear();
            editor.apply();

            intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

//    public String replaceHyphenWithSlash(String input) {
//        if (input == null) {
//            return "";
//        }
//        return input.replace("-", "/");
//    }
}