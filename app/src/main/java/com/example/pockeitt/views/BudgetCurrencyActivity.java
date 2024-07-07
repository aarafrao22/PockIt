package com.example.pockeitt.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pockeitt.R;
import com.google.android.material.textfield.TextInputLayout;

public class BudgetCurrencyActivity extends AppCompatActivity {
    TextInputLayout textfield;
    TextView designspinner;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budget_currency);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        textfield = findViewById(R.id.textfield);

        designspinner = findViewById(R.id.spinner_cat);
        textfield.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                textfield.setHint("US Dollar - USD");
            } else {
                textfield.setHint("Currency");
            }
        });
    }
}