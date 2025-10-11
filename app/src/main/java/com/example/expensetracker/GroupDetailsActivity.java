package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.fragments.BalancesFragment;
import com.example.expensetracker.fragments.ExpensesFragment;
import com.example.expensetracker.models.Group;
import com.example.expensetracker.utils.SMSHelper;
import com.example.expensetracker.models.Member;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GroupDetailsActivity extends AppCompatActivity {
    private TextView tvGroupName;
    private TextView tvGroupDescription;
    private TextView tvTotalExpenses;
    private TextView tvMemberCount;
    private ImageView btnBack;
    private ImageView btnAddMember;
    private FloatingActionButton fabAddExpense;
    private com.google.android.material.button.MaterialButton btnSendInvoice;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private DatabaseHelper databaseHelper;
    private SMSHelper smsHelper;
    private Group currentGroup;
    private int groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        initializeViews();
        initializeDatabase();
        setupClickListeners();
        loadGroupData();
        setupViewPager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Refresh data when returning to this activity
        loadGroupData();
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
        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupDescription = findViewById(R.id.tvGroupDescription);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        btnBack = findViewById(R.id.btnBack);
        btnAddMember = findViewById(R.id.btnAddMember);
        fabAddExpense = findViewById(R.id.fabAddExpense);
        btnSendInvoice = findViewById(R.id.btnSendInvoice);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
        smsHelper = new SMSHelper(this);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnAddMember.setOnClickListener(v -> showAddMemberDialog());
        
        fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("group_name", currentGroup.getGroupName());
            startActivity(intent);
        });
        
        btnSendInvoice.setOnClickListener(v -> sendInvoiceToMembers());
        
        // Add navigation to new activities
        
        findViewById(R.id.btnManageMembers).setOnClickListener(v -> {
            Intent intent = new Intent(this, MemberManagementActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("group_name", currentGroup.getGroupName());
            startActivity(intent);
        });
        
        findViewById(R.id.btnSettlement).setOnClickListener(v -> {
            Intent intent = new Intent(this, SettlementActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("group_name", currentGroup.getGroupName());
            startActivity(intent);
        });
    }

    private void loadGroupData() {
        Intent intent = getIntent();
        groupId = intent.getIntExtra("group_id", -1);
        String groupName = intent.getStringExtra("group_name");
        String groupDescription = intent.getStringExtra("group_description");

        if (groupId != -1) {
            currentGroup = databaseHelper.getGroup(groupId);
            if (currentGroup != null) {
                tvGroupName.setText(currentGroup.getGroupName());
                tvGroupDescription.setText(currentGroup.getDescription());
                
                // Calculate total expenses
                double totalExpenses = 0;
                for (com.example.expensetracker.models.Expense expense : databaseHelper.getExpensesForGroup(groupId)) {
                    totalExpenses += expense.getAmount();
                }
                tvTotalExpenses.setText(String.format("Total: $%.2f", totalExpenses));
                
                // Update member count
                List<Member> members = databaseHelper.getMembersForGroup(groupId);
                tvMemberCount.setText(members.size() + " members");
            }
        }
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, groupId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Expenses");
                    break;
                case 1:
                    tab.setText("Balances");
                    break;
            }
        }).attach();
    }

    private void showAddMemberDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        builder.setView(dialogView);
        
        final com.google.android.material.textfield.TextInputEditText etMemberName = 
            dialogView.findViewById(R.id.etMemberName);
        final com.google.android.material.textfield.TextInputEditText etEmail = 
            dialogView.findViewById(R.id.etEmail);
        final com.google.android.material.textfield.TextInputEditText etPhoneNumber = 
            dialogView.findViewById(R.id.etPhoneNumber);
        final com.google.android.material.button.MaterialButton btnCancel = 
            dialogView.findViewById(R.id.btnCancel);
        final com.google.android.material.button.MaterialButton btnAdd = 
            dialogView.findViewById(R.id.btnAdd);
        
        android.app.AlertDialog dialog = builder.create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnAdd.setOnClickListener(v -> {
            String memberName = etMemberName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            
            if (!memberName.isEmpty()) {
                addNewMember(memberName, email, phoneNumber);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a member name", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }

    private void addNewMember(String memberName, String email, String phoneNumber) {
        Member newMember = new Member();
        newMember.setGroupId(groupId);
        newMember.setMemberName(memberName);
        newMember.setEmail(email.isEmpty() ? null : email);
        newMember.setPhoneNumber(phoneNumber.isEmpty() ? null : phoneNumber);
        newMember.setTotalOwed(0);
        newMember.setTotalOwing(0);
        newMember.setBalance(0);
        
        long memberId = databaseHelper.addMember(newMember);
        if (memberId != -1) {
            Toast.makeText(this, "Member added successfully", Toast.LENGTH_SHORT).show();
            loadGroupData(); // Refresh the member count
        } else {
            Toast.makeText(this, "Failed to add member", Toast.LENGTH_SHORT).show();
        }
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final int groupId;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int groupId) {
            super(fragmentActivity);
            this.groupId = groupId;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            try {
                switch (position) {
                    case 0:
                        return ExpensesFragment.newInstance(groupId);
                    case 1:
                        return BalancesFragment.newInstance(groupId);
                    default:
                        return ExpensesFragment.newInstance(groupId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Return a safe fragment if there's an error
                return ExpensesFragment.newInstance(groupId);
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    private void sendInvoiceToMembers() {
        if (!smsHelper.hasSMSPermission()) {
            Toast.makeText(this, "SMS permission required to send invoices", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Member> members = databaseHelper.getMembersForGroup(groupId);
        List<com.example.expensetracker.models.Expense> expenses = databaseHelper.getExpensesForGroup(groupId);
        
        if (members.isEmpty()) {
            Toast.makeText(this, "No members found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expenses.isEmpty()) {
            Toast.makeText(this, "No expenses found to send invoices for", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send balance reminders to all members
        smsHelper.sendBalanceReminder(members);
        Toast.makeText(this, "Balance reminders sent to all members", Toast.LENGTH_SHORT).show();
    }
}
