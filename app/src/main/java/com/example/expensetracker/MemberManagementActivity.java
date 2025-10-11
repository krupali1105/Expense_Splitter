package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.adapters.MemberManagementAdapter;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Member;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MemberManagementActivity extends AppCompatActivity implements MemberManagementAdapter.OnMemberClickListener {
    private RecyclerView recyclerViewMembers;
    private LinearLayout emptyStateLayout;
    private MaterialButton btnBack;
    private FloatingActionButton fabAddMember;
    
    private MemberManagementAdapter memberAdapter;
    private DatabaseHelper databaseHelper;
    private List<Member> members;
    private int groupId;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_management);
        
        initializeViews();
        initializeDatabase();
        loadGroupData();
        setupRecyclerView();
        setupClickListeners();
        loadMembers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMembers();
    }

    private void initializeViews() {
        recyclerViewMembers = findViewById(R.id.recyclerViewMembers);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnBack = findViewById(R.id.btnBack);
        fabAddMember = findViewById(R.id.fabAddMember);
    }

    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void loadGroupData() {
        Intent intent = getIntent();
        groupId = intent.getIntExtra("group_id", -1);
        groupName = intent.getStringExtra("group_name");
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(groupName + " - Members");
        }
    }

    private void setupRecyclerView() {
        members = new ArrayList<>();
        memberAdapter = new MemberManagementAdapter(this, members, this);
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMembers.setAdapter(memberAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabAddMember.setOnClickListener(v -> showAddMemberDialog());
    }

    private void loadMembers() {
        members.clear();
        List<Member> groupMembers = databaseHelper.getMembersForGroup(groupId);
        if (groupMembers != null) {
            members.addAll(groupMembers);
        }
        memberAdapter.updateMembers(members);
        
        // Update UI based on data
        if (members.isEmpty()) {
            recyclerViewMembers.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewMembers.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
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
            
            if (memberName.isEmpty()) {
                etMemberName.setError("Member name is required");
                etMemberName.requestFocus();
                return;
            }
            
            addNewMember(memberName, email, phoneNumber);
            dialog.dismiss();
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
            loadMembers();
        } else {
            Toast.makeText(this, "Failed to add member", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMemberClick(Member member) {
        // Just show member details, no action needed
    }

    @Override
    public void onMemberEdit(Member member) {
        showEditMemberDialog(member);
    }

    @Override
    public void onMemberDelete(Member member) {
        showDeleteConfirmationDialog(member);
    }

    private void showEditMemberDialog(Member member) {
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
        
        // Pre-fill with existing data
        etMemberName.setText(member.getMemberName());
        etEmail.setText(member.getEmail() != null ? member.getEmail() : "");
        etPhoneNumber.setText(member.getPhoneNumber() != null ? member.getPhoneNumber() : "");
        btnAdd.setText("Update");
        
        android.app.AlertDialog dialog = builder.create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnAdd.setOnClickListener(v -> {
            String memberName = etMemberName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            
            if (memberName.isEmpty()) {
                etMemberName.setError("Member name is required");
                etMemberName.requestFocus();
                return;
            }
            
            updateMember(member, memberName, email, phoneNumber);
            dialog.dismiss();
        });
        
        dialog.show();
    }

    private void updateMember(Member member, String memberName, String email, String phoneNumber) {
        member.setMemberName(memberName);
        member.setEmail(email.isEmpty() ? null : email);
        member.setPhoneNumber(phoneNumber.isEmpty() ? null : phoneNumber);
        
        boolean updated = databaseHelper.updateMember(member);
        if (updated) {
            Toast.makeText(this, "Member updated successfully", Toast.LENGTH_SHORT).show();
            loadMembers();
        } else {
            Toast.makeText(this, "Failed to update member", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(Member member) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Member")
            .setMessage("Are you sure you want to delete " + member.getMemberName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                boolean deleted = databaseHelper.deleteMember(member.getMemberId());
                if (deleted) {
                    Toast.makeText(this, "Member deleted successfully", Toast.LENGTH_SHORT).show();
                    loadMembers();
                } else {
                    Toast.makeText(this, "Failed to delete member", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
