package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        Log.d("SettlementActivity", "=== LOADING SETTLEMENTS ===");
        settlements.clear();
        
        // Load existing settlements from database
        List<Settlement> existingSettlements = databaseHelper.getSettlementsForGroup(groupId);
        Log.d("SettlementActivity", "Found " + (existingSettlements != null ? existingSettlements.size() : 0) + " existing settlements in database");
        
        if (existingSettlements != null && !existingSettlements.isEmpty()) {
            // Add all existing settlements to the list
            settlements.addAll(existingSettlements);
            Log.d("SettlementActivity", "Loaded " + existingSettlements.size() + " existing settlements from database");
            
            // Check if we need to generate new unsettled settlements
            boolean hasUnsettledSettlements = false;
            int settledCount = 0;
            int unsettledCount = 0;
            
            for (Settlement settlement : existingSettlements) {
                if (settlement.isSettled()) {
                    settledCount++;
                } else {
                    unsettledCount++;
                    hasUnsettledSettlements = true;
                }
            }
            
            Log.d("SettlementActivity", "Settlement status: " + settledCount + " settled, " + unsettledCount + " unsettled");
            
            // If no unsettled settlements exist, generate new ones
            if (!hasUnsettledSettlements) {
                Log.d("SettlementActivity", "No unsettled settlements found, generating new ones");
                generateNewSettlements();
            } else {
                Log.d("SettlementActivity", "Found unsettled settlements, not generating new ones");
            }
        } else {
            // If no existing settlements, calculate new ones
            Log.d("SettlementActivity", "No existing settlements found, generating new ones");
            generateNewSettlements();
        }
        
        Log.d("SettlementActivity", "Total settlements in list: " + settlements.size());
        settlementAdapter.updateSettlements(settlements);
        
        // Update UI based on data
        if (settlements.isEmpty()) {
            recyclerViewSettlements.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewSettlements.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
        
        Log.d("SettlementActivity", "=== SETTLEMENTS LOADED ===");
    }
    
    private void generateNewSettlements() {
        Log.d("SettlementActivity", "=== GENERATING NEW SETTLEMENTS ===");
        List<Member> members = databaseHelper.getMembersForGroup(groupId);
        if (members != null && !members.isEmpty()) {
            List<Settlement> settlementSuggestions = calculateSettlements(members);
            Log.d("SettlementActivity", "Calculated " + settlementSuggestions.size() + " new settlements");
            
            // Save new settlements to database first
            for (Settlement settlement : settlementSuggestions) {
                long result = databaseHelper.addSettlement(settlement, groupId);
                Log.d("SettlementActivity", "Saved settlement to DB: " + settlement.getFromMember() + " -> " + settlement.getToMember() + " $" + settlement.getAmount() + " (ID: " + result + ")");
            }
            
            // Then add them to the current list
            settlements.addAll(settlementSuggestions);
            Log.d("SettlementActivity", "Added " + settlementSuggestions.size() + " settlements to list. Total settlements: " + settlements.size());
        }
        Log.d("SettlementActivity", "=== NEW SETTLEMENTS GENERATED ===");
    }

    private List<Settlement> calculateSettlements(List<Member> members) {
        List<Settlement> settlements = new ArrayList<>();
        
        // Create working copies of balances to avoid modifying original data
        List<Member> workingMembers = new ArrayList<>();
        for (Member member : members) {
            Member workingMember = new Member();
            workingMember.setMemberId(member.getMemberId());
            workingMember.setMemberName(member.getMemberName());
            workingMember.setBalance(member.getBalance());
            workingMembers.add(workingMember);
        }
        
        // Separate members into debtors and creditors
        List<Member> debtors = new ArrayList<>();
        List<Member> creditors = new ArrayList<>();
        
        for (Member member : workingMembers) {
            if (member.getBalance() > 0.01) { // Use small threshold to avoid floating point issues
                debtors.add(member);
            } else if (member.getBalance() < -0.01) {
                creditors.add(member);
            }
        }
        
        // Sort debtors by balance (highest debt first)
        debtors.sort((a, b) -> Double.compare(b.getBalance(), a.getBalance()));
        
        // Sort creditors by balance (highest credit first)
        creditors.sort((a, b) -> Double.compare(Math.abs(a.getBalance()), Math.abs(b.getBalance())));
        
        // Create settlements using a more efficient algorithm
        for (Member debtor : debtors) {
            double remainingDebt = debtor.getBalance();
            
            if (remainingDebt <= 0.01) continue;
            
            for (Member creditor : creditors) {
                double remainingCredit = Math.abs(creditor.getBalance());
                
                if (remainingCredit <= 0.01 || remainingDebt <= 0.01) continue;
                
                double settlementAmount = Math.min(remainingDebt, remainingCredit);
                
                if (settlementAmount > 0.01) { // Only create settlement if amount is meaningful
                    Settlement settlement = new Settlement();
                    settlement.setFromMember(debtor.getMemberName());
                    settlement.setToMember(creditor.getMemberName());
                    settlement.setAmount(settlementAmount);
                    settlement.setFromMemberId(debtor.getMemberId());
                    settlement.setToMemberId(creditor.getMemberId());
                    settlement.setSettled(false);
                    
                    settlements.add(settlement);
                    
                    // Update working balances
                    remainingDebt -= settlementAmount;
                    debtor.setBalance(remainingDebt);
                    creditor.setBalance(creditor.getBalance() + settlementAmount);
                }
            }
        }
        
        Log.d("SettlementActivity", "Generated " + settlements.size() + " settlements");
        return settlements;
    }

    private void recalculateBalances() {
        // Debug: Show current state before recalculation
        databaseHelper.debugBalanceCalculation(groupId);
        
        // Recalculate balances but preserve settled settlements
        databaseHelper.recalculateAllBalancesForGroup(groupId);
        
        // Only remove unsettled settlements and regenerate them
        databaseHelper.clearUnsettledSettlementsForGroup(groupId);
        
        // Debug: Show state after recalculation
        databaseHelper.debugBalanceCalculation(groupId);
        
        loadSettlements();
        Toast.makeText(this, "Balances recalculated (preserving settled settlements)", Toast.LENGTH_LONG).show();
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
                Log.d("SettlementActivity", "Marking settlement as settled: " + settlement.getFromMember() + " -> " + settlement.getToMember() + " $" + settlement.getAmount());
                
                // Update settlement status in database
                boolean updated = databaseHelper.updateSettlementStatus(
                    groupId, 
                    settlement.getFromMember(), 
                    settlement.getToMember(), 
                    settlement.getAmount(), 
                    true
                );
                
                if (updated) {
                    Log.d("SettlementActivity", "Settlement marked as completed successfully");
                    Toast.makeText(this, "Settlement marked as completed. Member balances have been updated.", Toast.LENGTH_LONG).show();
                    // Refresh settlements to show updated status
                    loadSettlements();
                } else {
                    Log.e("SettlementActivity", "Failed to update settlement: " + settlement.getFromMember() + " -> " + settlement.getToMember() + " $" + settlement.getAmount());
                    Toast.makeText(this, "Failed to update settlement. Check logs for details.", Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}

