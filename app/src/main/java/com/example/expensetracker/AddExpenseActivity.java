package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Expense;
import com.example.expensetracker.models.Member;
import com.example.expensetracker.utils.NotificationHelper;
import com.example.expensetracker.utils.LocationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {
    private TextInputEditText etExpenseName;
    private TextInputEditText etAmount;
    private TextInputEditText etDate;
    private TextInputEditText etDescription;
    private TextInputEditText etCategory;
    private MaterialButton btnSaveExpense;
    private ImageView btnBack;
    private DatabaseHelper databaseHelper;
    private NotificationHelper notificationHelper;
    private int groupId;
    private String groupName;
    private Calendar selectedDate;
    
    // New UI components for expense splitting
    private RadioGroup radioGroupSplitType;
    private RadioButton radioEqual;
    private RadioButton radioCustom;
    private Spinner spinnerPayer;
    private LinearLayout layoutParticipants;
    private LinearLayout layoutCustomAmounts;
    private TextView tvSplitSummary;
    
    // Data
    private List<Member> members;
    private List<CheckBox> participantCheckBoxes;
    private Map<String, TextInputEditText> customAmountFields;
    private ArrayAdapter<String> payerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        initializeViews();
        initializeDatabase();
        setupClickListeners();
        loadGroupData();
        setupDatePicker();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private void initializeViews() {
        etExpenseName = findViewById(R.id.etExpenseName);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);
        btnBack = findViewById(R.id.btnBack);
        selectedDate = Calendar.getInstance();
        
        // Initialize new UI components
        radioGroupSplitType = findViewById(R.id.radioGroupSplitType);
        radioEqual = findViewById(R.id.radioEqual);
        radioCustom = findViewById(R.id.radioCustom);
        spinnerPayer = findViewById(R.id.spinnerPayer);
        layoutParticipants = findViewById(R.id.layoutParticipants);
        layoutCustomAmounts = findViewById(R.id.layoutCustomAmounts);
        tvSplitSummary = findViewById(R.id.tvSplitSummary);
        
        // Initialize data structures
        members = new ArrayList<>();
        participantCheckBoxes = new ArrayList<>();
        customAmountFields = new HashMap<>();
    }

    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
        notificationHelper = new NotificationHelper(this);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnSaveExpense.setOnClickListener(v -> saveExpense());
        
        etDate.setOnClickListener(v -> showDatePicker());
        
        // Setup radio button listeners
        radioGroupSplitType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioEqual) {
                layoutCustomAmounts.setVisibility(View.GONE);
                updateSplitSummary();
            } else if (checkedId == R.id.radioCustom) {
                layoutCustomAmounts.setVisibility(View.VISIBLE);
                setupCustomAmountFields();
            }
        });
        
        // Setup amount change listener
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                updateSplitSummary();
            }
        });
    }

    private void loadGroupData() {
        Intent intent = getIntent();
        groupId = intent.getIntExtra("group_id", -1);
        groupName = intent.getStringExtra("group_name");
        
        if (groupId == -1) {
            Toast.makeText(this, "Invalid group", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Load members for this group
        loadMembers();
    }
    
    private void loadMembers() {
        members.clear();
        members.addAll(databaseHelper.getMembersForGroup(groupId));
        
        if (members.isEmpty()) {
            Toast.makeText(this, "No members found. Please add members first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        setupPayerSpinner();
        setupParticipantCheckBoxes();
    }
    
    private void setupPayerSpinner() {
        List<String> memberNames = new ArrayList<>();
        for (Member member : members) {
            memberNames.add(member.getMemberName());
        }
        
        payerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, memberNames);
        payerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayer.setAdapter(payerAdapter);
    }
    
    private void setupParticipantCheckBoxes() {
        layoutParticipants.removeAllViews();
        participantCheckBoxes.clear();
        
        for (Member member : members) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(member.getMemberName());
            checkBox.setChecked(true); // Default to checked
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateSplitSummary());
            
            layoutParticipants.addView(checkBox);
            participantCheckBoxes.add(checkBox);
        }
        
        updateSplitSummary();
    }
    
    private void setupCustomAmountFields() {
        layoutCustomAmounts.removeAllViews();
        customAmountFields.clear();
        
        for (CheckBox checkBox : participantCheckBoxes) {
            if (checkBox.isChecked()) {
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setPadding(0, 8, 0, 8);
                
                TextView label = new TextView(this);
                label.setText(checkBox.getText() + ": $");
                label.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                
                TextInputEditText amountField = new TextInputEditText(this);
                amountField.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
                amountField.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                amountField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    
                    @Override
                    public void afterTextChanged(Editable s) {
                        updateSplitSummary();
                    }
                });
                
                rowLayout.addView(label);
                rowLayout.addView(amountField);
                layoutCustomAmounts.addView(rowLayout);
                
                customAmountFields.put(checkBox.getText().toString(), amountField);
            }
        }
    }
    
    private void updateSplitSummary() {
        String amountText = etAmount.getText().toString().trim();
        if (amountText.isEmpty()) {
            tvSplitSummary.setVisibility(View.GONE);
            return;
        }
        
        try {
            double totalAmount = Double.parseDouble(amountText);
            List<String> selectedParticipants = getSelectedParticipants();
            
            if (selectedParticipants.isEmpty()) {
                tvSplitSummary.setVisibility(View.GONE);
                return;
            }
            
            StringBuilder summary = new StringBuilder();
            summary.append("Split Summary:\n");
            
            if (radioEqual.isChecked()) {
                double amountPerPerson = totalAmount / selectedParticipants.size();
                summary.append(String.format("$%.2f each\n", amountPerPerson));
                summary.append("Participants: ").append(String.join(", ", selectedParticipants));
            } else {
                // Custom split
                double customTotal = 0;
                for (String participant : selectedParticipants) {
                    TextInputEditText field = customAmountFields.get(participant);
                    if (field != null && !field.getText().toString().trim().isEmpty()) {
                        try {
                            customTotal += Double.parseDouble(field.getText().toString().trim());
                        } catch (NumberFormatException e) {
                            // Ignore invalid numbers
                        }
                    }
                }
                
                if (Math.abs(customTotal - totalAmount) > 0.01) {
                    summary.append("⚠️ Custom amounts don't match total!\n");
                }
                
                summary.append("Custom amounts:\n");
                for (String participant : selectedParticipants) {
                    TextInputEditText field = customAmountFields.get(participant);
                    String amount = field != null ? field.getText().toString().trim() : "0";
                    summary.append(String.format("%s: $%s\n", participant, amount));
                }
            }
            
            tvSplitSummary.setText(summary.toString());
            tvSplitSummary.setVisibility(View.VISIBLE);
            
        } catch (NumberFormatException e) {
            tvSplitSummary.setVisibility(View.GONE);
        }
    }
    
    private List<String> getSelectedParticipants() {
        List<String> selected = new ArrayList<>();
        for (CheckBox checkBox : participantCheckBoxes) {
            if (checkBox.isChecked()) {
                selected.add(checkBox.getText().toString());
            }
        }
        return selected;
    }

    private void setupDatePicker() {
        // Set default date to today
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        etDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                etDate.setText(dateFormat.format(selectedDate.getTime()));
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveExpense() {
        // Validate inputs
        String expenseName = etExpenseName.getText().toString().trim();
        String amountText = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        if (expenseName.isEmpty()) {
            etExpenseName.setError("Expense name is required");
            etExpenseName.requestFocus();
            return;
        }

        if (amountText.isEmpty()) {
            etAmount.setError("Amount is required");
            etAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                etAmount.setError("Amount must be greater than 0");
                etAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount");
            etAmount.requestFocus();
            return;
        }

        // Get payer from spinner
        String payer = spinnerPayer.getSelectedItem().toString();
        if (payer == null || payer.isEmpty()) {
            Toast.makeText(this, "Please select a payer", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected participants
        List<String> selectedParticipants = getSelectedParticipants();
        if (selectedParticipants.isEmpty()) {
            Toast.makeText(this, "Please select at least one participant", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate custom amounts if custom split is selected
        if (radioCustom.isChecked()) {
            double customTotal = 0;
            for (String participant : selectedParticipants) {
                TextInputEditText field = customAmountFields.get(participant);
                if (field != null && !field.getText().toString().trim().isEmpty()) {
                    try {
                        customTotal += Double.parseDouble(field.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid custom amount for " + participant, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            
            if (Math.abs(customTotal - amount) > 0.01) {
                Toast.makeText(this, "Custom amounts must equal the total amount", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create participants string
        String participants = String.join(", ", selectedParticipants);
        
        // Create participant amounts string
        String participantAmounts = "";
        if (radioEqual.isChecked()) {
            // Equal split
            double amountPerPerson = amount / selectedParticipants.size();
            String[] amounts = new String[selectedParticipants.size()];
            for (int i = 0; i < selectedParticipants.size(); i++) {
                amounts[i] = String.valueOf(amountPerPerson);
            }
            participantAmounts = String.join(", ", amounts);
        } else {
            // Custom split
            String[] amounts = new String[selectedParticipants.size()];
            for (int i = 0; i < selectedParticipants.size(); i++) {
                String participant = selectedParticipants.get(i);
                TextInputEditText field = customAmountFields.get(participant);
                String amountStr = field != null ? field.getText().toString().trim() : "0";
                amounts[i] = amountStr;
            }
            participantAmounts = String.join(", ", amounts);
        }

        // Create expense object
        Expense expense = new Expense();
        expense.setGroupId(groupId);
        expense.setExpenseName(expenseName);
        expense.setAmount(amount);
        expense.setPayer(payer);
        expense.setParticipants(participants);
        expense.setParticipantAmounts(participantAmounts);
        expense.setDescription(description.isEmpty() ? null : description);
        expense.setCategory(category.isEmpty() ? "General" : category);
        
        // Format date for database
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        expense.setDate(dbDateFormat.format(selectedDate.getTime()));

        // Get current location and save expense
        getCurrentLocation(expense);
    }

    private void getCurrentLocation(Expense expense) {
        LocationHelper locationHelper = new LocationHelper(this);
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationReceived(android.location.Location location, String address) {
                expense.setLatitude(location.getLatitude());
                expense.setLongitude(location.getLongitude());
                expense.setLocation(address);
                saveExpenseToDatabase(expense);
            }

            @Override
            public void onLocationError(String error) {
                // Set default location if location access fails
                expense.setLatitude(0.0);
                expense.setLongitude(0.0);
                expense.setLocation("Location not available");
                saveExpenseToDatabase(expense);
            }
        });
    }

    private void saveExpenseToDatabase(Expense expense) {
        try {
            // Save to database
            long expenseId = databaseHelper.addExpense(expense);
            if (expenseId != -1) {
                Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                notificationHelper.showExpenseAddedNotification(groupName, expense.getExpenseName(), expense.getAmount());
                
                // Return to group details
                Intent resultIntent = new Intent();
                resultIntent.putExtra("expense_added", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving expense: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
