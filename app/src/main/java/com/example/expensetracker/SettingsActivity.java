package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Switch switchNotifications;
    private Switch switchLocationTracking;
    private Switch switchSMSReminders;
    private Switch switchDarkMode;
    private TextView tvCurrency;
    private TextView tvDefaultSplit;
    private MaterialButton btnChangeCurrency;
    private MaterialButton btnChangeSplit;
    private MaterialButton btnExportData;
    private MaterialButton btnImportData;
    private MaterialButton btnClearData;
    private MaterialButton btnAbout;
    private MaterialCardView cardNotifications;
    private MaterialCardView cardPrivacy;
    private MaterialCardView cardData;
    private MaterialCardView cardAppearance;
    
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "expense_tracker_settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initializeViews();
        setupToolbar();
        loadSettings();
        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchLocationTracking = findViewById(R.id.switchLocationTracking);
        switchSMSReminders = findViewById(R.id.switchSMSReminders);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        tvCurrency = findViewById(R.id.tvCurrency);
        tvDefaultSplit = findViewById(R.id.tvDefaultSplit);
        btnChangeCurrency = findViewById(R.id.btnChangeCurrency);
        btnChangeSplit = findViewById(R.id.btnChangeSplit);
        btnExportData = findViewById(R.id.btnExportData);
        btnImportData = findViewById(R.id.btnImportData);
        btnClearData = findViewById(R.id.btnClearData);
        btnAbout = findViewById(R.id.btnAbout);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardPrivacy = findViewById(R.id.cardPrivacy);
        cardData = findViewById(R.id.cardData);
        cardAppearance = findViewById(R.id.cardAppearance);
        
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadSettings() {
        // Load notification settings
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        boolean locationTracking = sharedPreferences.getBoolean("location_tracking", true);
        boolean smsReminders = sharedPreferences.getBoolean("sms_reminders", false);
        boolean darkMode = sharedPreferences.getBoolean("dark_mode", false);
        
        switchNotifications.setChecked(notificationsEnabled);
        switchLocationTracking.setChecked(locationTracking);
        switchSMSReminders.setChecked(smsReminders);
        switchDarkMode.setChecked(darkMode);
        
        // Load currency and split settings
        String currency = sharedPreferences.getString("currency", "USD ($)");
        String defaultSplit = sharedPreferences.getString("default_split", "Equal");
        
        tvCurrency.setText(currency);
        tvDefaultSplit.setText(defaultSplit);
    }

    private void setupClickListeners() {
        // Notification settings
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            showToast(isChecked ? "Notifications enabled" : "Notifications disabled");
        });

        switchLocationTracking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("location_tracking", isChecked).apply();
            showToast(isChecked ? "Location tracking enabled" : "Location tracking disabled");
        });

        switchSMSReminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("sms_reminders", isChecked).apply();
            showToast(isChecked ? "SMS reminders enabled" : "SMS reminders disabled");
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            showToast(isChecked ? "Dark mode enabled" : "Dark mode disabled");
            // Note: Dark mode change requires app restart for full effect
        });

        // Currency settings
        btnChangeCurrency.setOnClickListener(v -> showCurrencyDialog());
        
        // Split settings
        btnChangeSplit.setOnClickListener(v -> showSplitDialog());
        
        // Data management
        btnExportData.setOnClickListener(v -> {
            showToast("Export functionality coming soon!");
            // TODO: Implement data export
        });
        
        btnImportData.setOnClickListener(v -> {
            showToast("Import functionality coming soon!");
            // TODO: Implement data import
        });
        
        btnClearData.setOnClickListener(v -> showClearDataDialog());
        
        // About
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void showCurrencyDialog() {
        String[] currencies = {"USD ($)", "EUR (€)", "GBP (£)", "INR (₹)", "CAD (C$)", "AUD (A$)"};
        String currentCurrency = sharedPreferences.getString("currency", "USD ($)");
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Currency")
                .setSingleChoiceItems(currencies, getCurrencyIndex(currentCurrency), (dialog, which) -> {
                    String selectedCurrency = currencies[which];
                    sharedPreferences.edit().putString("currency", selectedCurrency).apply();
                    tvCurrency.setText(selectedCurrency);
                    showToast("Currency changed to " + selectedCurrency);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSplitDialog() {
        String[] splitOptions = {"Equal", "Custom", "Percentage"};
        String currentSplit = sharedPreferences.getString("default_split", "Equal");
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Default Split Type")
                .setSingleChoiceItems(splitOptions, getSplitIndex(currentSplit), (dialog, which) -> {
                    String selectedSplit = splitOptions[which];
                    sharedPreferences.edit().putString("default_split", selectedSplit).apply();
                    tvDefaultSplit.setText(selectedSplit);
                    showToast("Default split changed to " + selectedSplit);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showClearDataDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Clear All Data")
                .setMessage("This will permanently delete all groups, expenses, and members. This action cannot be undone.")
                .setPositiveButton("Clear All Data", (dialog, which) -> {
                    // Clear all data
                    clearAllData();
                    showToast("All data has been cleared");
                    // Return to home
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearAllData() {
        // Clear database
        com.example.expensetracker.database.DatabaseHelper dbHelper = new com.example.expensetracker.database.DatabaseHelper(this);
        dbHelper.clearAllData();
        dbHelper.close();
        
        // Clear settings (except currency and split)
        String currency = sharedPreferences.getString("currency", "USD ($)");
        String defaultSplit = sharedPreferences.getString("default_split", "Equal");
        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit()
                .putString("currency", currency)
                .putString("default_split", defaultSplit)
                .apply();
    }

    private int getCurrencyIndex(String currency) {
        String[] currencies = {"USD ($)", "EUR (€)", "GBP (£)", "INR (₹)", "CAD (C$)", "AUD (A$)"};
        for (int i = 0; i < currencies.length; i++) {
            if (currencies[i].equals(currency)) {
                return i;
            }
        }
        return 0;
    }

    private int getSplitIndex(String split) {
        String[] splitOptions = {"Equal", "Custom", "Percentage"};
        for (int i = 0; i < splitOptions.length; i++) {
            if (splitOptions[i].equals(split)) {
                return i;
            }
        }
        return 0;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
