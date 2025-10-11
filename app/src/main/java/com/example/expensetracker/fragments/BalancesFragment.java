package com.example.expensetracker.fragments;

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
import com.example.expensetracker.adapters.BalanceAdapter;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Member;

import java.util.ArrayList;
import java.util.List;

public class BalancesFragment extends Fragment implements BalanceAdapter.OnMemberClickListener {
    private static final String ARG_GROUP_ID = "group_id";
    
    private RecyclerView recyclerViewBalances;
    private LinearLayout emptyStateLayout;
    private BalanceAdapter balanceAdapter;
    private DatabaseHelper databaseHelper;
    private List<Member> members;
    private int groupId;

    public static BalancesFragment newInstance(int groupId) {
        BalancesFragment fragment = new BalancesFragment();
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
        View view = inflater.inflate(R.layout.fragment_balances, container, false);
        
        initializeViews(view);
        initializeDatabase();
        setupRecyclerView();
        loadBalances();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        loadBalances();
    }

    private void initializeViews(View view) {
        recyclerViewBalances = view.findViewById(R.id.recyclerViewBalances);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
    }

    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(getContext());
    }

    private void setupRecyclerView() {
        members = new ArrayList<>();
        balanceAdapter = new BalanceAdapter(getContext(), members);
        balanceAdapter.setOnMemberClickListener(this);
        recyclerViewBalances.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewBalances.setAdapter(balanceAdapter);
    }

    // In BalancesFragment.java

    private void loadBalances() {
        try {
            // Debug: Show current state before recalculation
            databaseHelper.debugBalanceCalculation(groupId);
            
            // This is the only call you need now to recalculate everything
            databaseHelper.recalculateAllBalancesForGroup(groupId);
            
            // Debug: Show state after recalculation
            databaseHelper.debugBalanceCalculation(groupId);

            members.clear();
            List<Member> groupMembers = databaseHelper.getMembersForGroup(groupId);
            if (groupMembers != null) {
                members.addAll(groupMembers);
            }
            balanceAdapter.updateMembers(members);

            // Update UI based on data
            if (members.isEmpty()) {
                recyclerViewBalances.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            } else {
                recyclerViewBalances.setVisibility(View.VISIBLE);
                emptyStateLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Show empty state if there's an error
            recyclerViewBalances.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMemberClick(Member member) {
        showPersonExpenseDialog(member);
    }

    private void showPersonExpenseDialog(Member member) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_person_expenses, null);
        builder.setView(dialogView);
        
        // Initialize views
        android.widget.TextView tvPersonName = dialogView.findViewById(R.id.tvPersonName);
        android.widget.TextView tvTotalBalance = dialogView.findViewById(R.id.tvTotalBalance);
        android.widget.TextView tvTotalOwed = dialogView.findViewById(R.id.tvTotalOwed);
        android.widget.TextView tvTotalOwing = dialogView.findViewById(R.id.tvTotalOwing);
        androidx.recyclerview.widget.RecyclerView recyclerViewExpenses = dialogView.findViewById(R.id.recyclerViewExpenses);
        com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.btnClose);
        com.google.android.material.button.MaterialButton btnSendReminder = dialogView.findViewById(R.id.btnSendReminder);
        
        // Set member data
        tvPersonName.setText(member.getMemberName());
        tvTotalBalance.setText("Balance: $" + String.format("%.2f", member.getBalance()));
        tvTotalOwed.setText("$" + String.format("%.2f", member.getTotalOwed()));
        tvTotalOwing.setText("$" + String.format("%.2f", member.getTotalOwing()));
        
        // Setup expenses list
        setupMemberExpenses(recyclerViewExpenses, member);
        
        android.app.AlertDialog dialog = builder.create();
        
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnSendReminder.setOnClickListener(v -> {
            dialog.dismiss();
            // TODO: Implement send reminder functionality
            android.widget.Toast.makeText(getContext(), "Reminder sent to " + member.getMemberName(), android.widget.Toast.LENGTH_SHORT).show();
        });
        
        dialog.show();
    }

    private void setupMemberExpenses(androidx.recyclerview.widget.RecyclerView recyclerView, Member member) {
        try {
            // Get expenses for this member
            java.util.List<com.example.expensetracker.models.Expense> memberExpenses = 
                databaseHelper.getExpensesForGroup(groupId);
            
            // Filter expenses where this member is involved
            java.util.List<com.example.expensetracker.models.Expense> filteredExpenses = new java.util.ArrayList<>();
            if (memberExpenses != null) {
                for (com.example.expensetracker.models.Expense expense : memberExpenses) {
                    if (expense != null && expense.getParticipants() != null && 
                        expense.getParticipants().contains(member.getMemberName())) {
                        filteredExpenses.add(expense);
                    }
                }
            }
            
            // Setup adapter
            com.example.expensetracker.adapters.ExpenseAdapter adapter = 
                new com.example.expensetracker.adapters.ExpenseAdapter(getContext(), filteredExpenses);
            recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            // If there's an error, just show empty list
            java.util.List<com.example.expensetracker.models.Expense> emptyList = new java.util.ArrayList<>();
            com.example.expensetracker.adapters.ExpenseAdapter adapter = 
                new com.example.expensetracker.adapters.ExpenseAdapter(getContext(), emptyList);
            recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
    }
}
