package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.adapters.SettlementAdapter;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Member;
import com.example.expensetracker.models.Settlement;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class SettlementActivity extends AppCompatActivity implements SettlementAdapter.OnSettlementClickListener {
    private RecyclerView recyclerViewSettlements;
    private LinearLayout emptyStateLayout;
    private MaterialButton btnBack;
    private MaterialButton btnRecalculate;
    
    private SettlementAdapter settlementAdapter;
    private DatabaseHelper databaseHelper;
    private List<Settlement> settlements;
    private int groupId;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        
        initializeViews();
        initializeDatabase();
        loadGroupData();
        setupRecyclerView();
        setupClickListeners();
        loadSettlements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettlements();
    }

    private void initializeViews() {
        recyclerViewSettlements = findViewById(R.id.recyclerViewSettlements);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnBack = findViewById(R.id.btnBack);
        btnRecalculate = findViewById(R.id.btnRecalculate);
    }

    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void loadGroupData() {
        Intent intent = getIntent();
        groupId = intent.getIntExtra("group_id", -1);
        groupName = intent.getStringExtra("group_name");
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(groupName + " - Settlement");
        }
    }

    private void setupRecyclerView() {
        settlements = new ArrayList<>();
        settlementAdapter = new SettlementAdapter(this, settlements, this);
        recyclerViewSettlements.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSettlements.setAdapter(settlementAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRecalculate.setOnClickListener(v -> recalculateBalances());
    }

    private void loadSettlements() {
        // First recalculate balances
        databaseHelper.recalculateAllBalancesForGroup(groupId);
        
        settlements.clear();
        List<Member> members = databaseHelper.getMembersForGroup(groupId);
        
        if (members != null && !members.isEmpty()) {
            // Create settlement suggestions
            List<Settlement> settlementSuggestions = calculateSettlements(members);
            settlements.addAll(settlementSuggestions);
        }
        
        settlementAdapter.updateSettlements(settlements);
        
        // Update UI based on data
        if (settlements.isEmpty()) {
            recyclerViewSettlements.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewSettlements.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    private List<Settlement> calculateSettlements(List<Member> members) {
        List<Settlement> settlements = new ArrayList<>();
        
        // Separate members into those who owe money and those who should get money
        List<Member> debtors = new ArrayList<>();
        List<Member> creditors = new ArrayList<>();
        
        for (Member member : members) {
            if (member.getBalance() > 0) {
                debtors.add(member); // People who owe money
            } else if (member.getBalance() < 0) {
                creditors.add(member); // People who should get money
            }
        }
        
        // Create settlement suggestions
        for (Member debtor : debtors) {
            double debtAmount = debtor.getBalance();
            
            for (Member creditor : creditors) {
                double creditAmount = Math.abs(creditor.getBalance());
                
                if (debtAmount > 0 && creditAmount > 0) {
                    double settlementAmount = Math.min(debtAmount, creditAmount);
                    
                    Settlement settlement = new Settlement();
                    settlement.setFromMember(debtor.getMemberName());
                    settlement.setToMember(creditor.getMemberName());
                    settlement.setAmount(settlementAmount);
                    settlement.setFromMemberId(debtor.getMemberId());
                    settlement.setToMemberId(creditor.getMemberId());
                    
                    settlements.add(settlement);
                    
                    debtAmount -= settlementAmount;
                    creditor.setBalance(creditor.getBalance() + settlementAmount);
                }
            }
        }
        
        return settlements;
    }

    private void recalculateBalances() {
        // Debug: Show current state before recalculation
        databaseHelper.debugBalanceCalculation(groupId);
        
        // Recalculate balances
        databaseHelper.recalculateAllBalancesForGroup(groupId);
        
        // Debug: Show state after recalculation
        databaseHelper.debugBalanceCalculation(groupId);
        
        loadSettlements();
        Toast.makeText(this, "Balances recalculated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSettlementClick(Settlement settlement) {
        showSettlementConfirmationDialog(settlement);
    }

    private void showSettlementConfirmationDialog(Settlement settlement) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Mark as Settled")
            .setMessage(settlement.getFromMember() + " should pay " + 
                       settlement.getToMember() + " $" + String.format("%.2f", settlement.getAmount()) + 
                       "\n\nMark this settlement as completed?")
            .setPositiveButton("Mark as Settled", (dialog, which) -> {
                // Here you would implement the settlement logic
                // For now, just show a success message
                Toast.makeText(this, "Settlement marked as completed", Toast.LENGTH_SHORT).show();
                loadSettlements(); // Refresh the list
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
