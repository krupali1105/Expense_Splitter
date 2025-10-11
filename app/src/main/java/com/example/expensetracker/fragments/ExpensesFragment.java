package com.example.expensetracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.adapters.ExpenseAdapter;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpensesFragment extends Fragment implements ExpenseAdapter.OnExpenseClickListener {
    private static final String ARG_GROUP_ID = "group_id";
    
    private RecyclerView recyclerViewExpenses;
    private LinearLayout emptyStateLayout;
    private ExpenseAdapter expenseAdapter;
    private DatabaseHelper databaseHelper;
    private List<Expense> expenses;
    private int groupId;

    public static ExpensesFragment newInstance(int groupId) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getInt(ARG_GROUP_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        
        initializeViews(view);
        initializeDatabase();
        setupRecyclerView();
        loadExpenses();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        loadExpenses();
    }

    private void initializeViews(View view) {
        recyclerViewExpenses = view.findViewById(R.id.recyclerViewExpenses);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
    }

    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(getContext());
    }

    private void setupRecyclerView() {
        expenses = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(getContext(), expenses);
        expenseAdapter.setOnExpenseClickListener(this);
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewExpenses.setAdapter(expenseAdapter);
    }

    private void loadExpenses() {
        expenses.clear();
        expenses.addAll(databaseHelper.getExpensesForGroup(groupId));
        expenseAdapter.updateExpenses(expenses);
        
        // Update UI based on data
        if (expenses.isEmpty()) {
            recyclerViewExpenses.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewExpenses.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onExpenseClick(Expense expense) {
        showExpenseDetailDialog(expense);
    }

    private void showExpenseDetailDialog(Expense expense) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_expense_detail, null);
        builder.setView(dialogView);
        
        // Initialize views
        android.widget.TextView tvExpenseName = dialogView.findViewById(R.id.tvExpenseName);
        android.widget.TextView tvAmount = dialogView.findViewById(R.id.tvAmount);
        android.widget.TextView tvPayer = dialogView.findViewById(R.id.tvPayer);
        android.widget.TextView tvDate = dialogView.findViewById(R.id.tvDate);
        android.widget.TextView tvCategory = dialogView.findViewById(R.id.tvCategory);
        android.widget.TextView tvLocation = dialogView.findViewById(R.id.tvLocation);
        android.widget.TextView tvSplitType = dialogView.findViewById(R.id.tvSplitType);
        android.widget.TextView tvParticipants = dialogView.findViewById(R.id.tvParticipants);
        androidx.recyclerview.widget.RecyclerView recyclerViewParticipants = dialogView.findViewById(R.id.recyclerViewParticipants);
        com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.btnClose);
        com.google.android.material.button.MaterialButton btnEdit = dialogView.findViewById(R.id.btnEdit);
        
        // Set expense data
        tvExpenseName.setText(expense.getExpenseName());
        tvAmount.setText("$" + String.format("%.2f", expense.getAmount()));
        tvPayer.setText(expense.getPayer());
        tvDate.setText(expense.getDate());
        tvCategory.setText(expense.getCategory() != null ? expense.getCategory() : "General");
        tvLocation.setText(expense.getLocation() != null ? expense.getLocation() : "Not specified");
        
        // Determine split type
        boolean isCustomSplit = expense.getParticipantAmounts() != null && !expense.getParticipantAmounts().isEmpty();
        tvSplitType.setText("Split Type: " + (isCustomSplit ? "Custom" : "Equal"));
        tvParticipants.setText("Participants: " + expense.getParticipants());
        
        // Setup participant breakdown
        setupParticipantBreakdown(recyclerViewParticipants, expense);
        
        android.app.AlertDialog dialog = builder.create();
        
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnEdit.setOnClickListener(v -> {
            dialog.dismiss();
            // TODO: Implement edit functionality
            android.widget.Toast.makeText(getContext(), "Edit functionality coming soon!", android.widget.Toast.LENGTH_SHORT).show();
        });
        
        dialog.show();
    }

    private void setupParticipantBreakdown(androidx.recyclerview.widget.RecyclerView recyclerView, Expense expense) {
        // Create participant breakdown data
        java.util.List<com.example.expensetracker.models.ParticipantBreakdown> participantBreakdowns = new java.util.ArrayList<>();
        
        String[] participants = expense.getParticipants().split(",");
        String[] participantAmounts = null;
        
        if (expense.getParticipantAmounts() != null && !expense.getParticipantAmounts().isEmpty()) {
            participantAmounts = expense.getParticipantAmounts().split(",");
        }
        
        for (int i = 0; i < participants.length; i++) {
            String participant = participants[i].trim();
            double amount;
            
            if (participantAmounts != null && i < participantAmounts.length) {
                try {
                    amount = Double.parseDouble(participantAmounts[i].trim());
                } catch (NumberFormatException e) {
                    amount = expense.getAmount() / participants.length;
                }
            } else {
                amount = expense.getAmount() / participants.length;
            }
            
            double amountPaid = participant.equals(expense.getPayer()) ? expense.getAmount() : 0;
            double netBalance = amount - amountPaid;
            
            participantBreakdowns.add(new com.example.expensetracker.models.ParticipantBreakdown(
                participant, amountPaid, amount, netBalance
            ));
        }
        
        // Setup adapter
        com.example.expensetracker.adapters.ParticipantBreakdownAdapter adapter = 
            new com.example.expensetracker.adapters.ParticipantBreakdownAdapter(getContext(), participantBreakdowns);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
