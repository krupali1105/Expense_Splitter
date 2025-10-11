package com.example.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView tvAppName;
    private TextView tvVersion;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeViews();
        setupClickListeners();
        loadAppInfo();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvAppName = findViewById(R.id.tvAppName);
        tvVersion = findViewById(R.id.tvVersion);
        tvDescription = findViewById(R.id.tvDescription);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadAppInfo() {
        tvAppName.setText("Expense Tracker Pro");
        tvVersion.setText("Version 2.0.0");
        tvDescription.setText("A powerful expense tracking app that helps you manage shared expenses with friends, family, and colleagues. Track who owes what, split bills fairly, and never lose track of shared expenses again.\n\n" +
                "Features:\n" +
                "• Smart expense splitting\n" +
                "• Location tracking\n" +
                "• SMS notifications\n" +
                "• Detailed balance reports\n" +
                "• Beautiful dark mode\n" +
                "• Export capabilities\n\n" +
                "Made with ❤️ for better expense management");
    }
}
