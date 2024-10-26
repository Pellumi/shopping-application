package com.example.mobiledesignproject.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.mobiledesignproject.MainActivity;
import com.example.mobiledesignproject.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UIMethods {

    private Context context;
    private Intent intent;

    public UIMethods(){}

    public UIMethods(Context context, Intent intent){
        this.context = context;
        this.intent = intent;
    }

    public void changeUI(EditText text){
        text.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                text.setBackgroundResource(R.drawable.edit_text_clear);
            } else if(text.getText().toString().isEmpty()){
                text.setBackgroundResource(R.drawable.edit_text_clear);
            }
        });
    }

    public String replaceSlashWithHyphen(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("/", "-");
    }

    public String replaceHyphenWithSlash(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("-", "/");
    }

    public String removeSymbol(String input) {
        if (input == null) {
            return "";
        }

        String withoutComma = input.replace(",", "");
        return withoutComma.replace("₦", "");
    }

    public void togglePassword(EditText editText, TextView textView, boolean isPasswordVisible){
        if (isPasswordVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            textView.setText("Hide Password");
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            textView.setText("Show Password");
        }
        editText.setSelection(editText.getText().length());
    }

    public String formatCurrency(String numberString) {
        if (numberString == null || numberString.isEmpty()) {
            return "";
        }

        try {
            String[] parts = numberString.split("\\.");
            String integerPart = parts[0];

            StringBuilder formattedInteger = new StringBuilder();
            int length = integerPart.length();

            for (int i = 0; i < length; i++) {
                formattedInteger.append(integerPart.charAt(i));
                int remaining = length - i - 1;
                if (remaining > 0 && remaining % 3 == 0) {
                    formattedInteger.append(",");
                }
            }

            String formattedNumber = formattedInteger.toString();

            if (parts.length > 1) {
                formattedNumber += "." + parts[1];
            }

            return "₦" + formattedNumber;
        } catch (NumberFormatException e) {
            return "Invalid Input";
        }
    }


    public void showRegularSnackBar(Context context, String message, int colorId, int layoutId){
        LinearLayout parentLayout = ((Activity) context).findViewById(layoutId);
        if (parentLayout == null) {
            Log.e("SnackBar", "Parent layout not found");
            return;
        }

        Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);

        View snackbarView = snackbar.getView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.gravity = Gravity.TOP;
        params.setMargins(0, 0, 0, 0);
        snackbarView.setLayoutParams(params);

        snackbarView.setBackgroundColor(ContextCompat.getColor(context, colorId));
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        TextView actionTextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_action);
        actionTextView.setTextColor(Color.WHITE);

        snackbar.show();
    }

    public void showLowerRegularSnackBar(Context context, String message, int colorId, int layoutId){
        LinearLayout parentLayout = ((Activity) context).findViewById(layoutId);
        if (parentLayout == null) {
            Log.e("SnackBar", "Parent layout not found");
            return;
        }

        Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);

        View snackbarView = snackbar.getView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.gravity = Gravity.TOP;
        params.setMargins(0, 60, 0, 0);
        snackbarView.setLayoutParams(params);

        snackbarView.setBackgroundColor(ContextCompat.getColor(context, colorId));
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        TextView actionTextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_action);
        actionTextView.setTextColor(Color.WHITE);

        snackbar.show();
    }

    public void showActionSnackBar(Context context, String message, String action, int id){
        LinearLayout parentLayout = ((Activity) context).findViewById(R.id.main);
        if (parentLayout == null) {
            Log.e("SnackBar", "Parent layout not found");
            return;
        }

        Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG)
                .setAction(action, v -> {
                    intent = new Intent(context, MainActivity.class);
                    intent.setData(Uri.parse("myapp://navigation_cart"));
                    context.startActivity(intent);
                });

        View snackbarView = snackbar.getView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.gravity = Gravity.TOP;
        params.setMargins(0, 60, 0, 0);
        snackbarView.setLayoutParams(params);

        snackbarView.setBackgroundColor(ContextCompat.getColor(context, id));
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        TextView actionTextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_action);
        actionTextView.setTextColor(Color.WHITE);


        snackbar.show();
    }

    public String capitalizeWord(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s+");

        for (String word : words) {
            if (word.length() > 1) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            } else {
                result.append(word.toUpperCase());
            }
            result.append(" ");
        }

        return result.toString().trim();
    }

    public String convertDate(String isoDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");

        try {
            Date date = inputFormat.parse(isoDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFirstThreeChars(String input){
        if(input == null || input.length() < 3){
            return input;
        }

        return input.substring(0, 3);
    }
}
