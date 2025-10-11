package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Group;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditGroupActivity extends AppCompatActivity {
    private TextInputEditText etGroupName;
    private TextInputEditText etGroupDescription;
    private MaterialButton btnSave;
    private MaterialButton btnDelete;
    private MaterialButton btnCancel;
    
    private DatabaseHelper databaseHelper;
    private Group currentGroup;
    private int groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        
        initializeViews();
        initializeDatabase();
        loadGroupData();
        setupClickListeners();
    }

    private void initializeViews() {
        etGroupName = findViewById(R.id.etGroupName);
        etGroupDescription = findViewById(R.id.etGroupDescription);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void loadGroupData() {
        Intent intent = getIntent();
        groupId = intent.getIntExtra("group_id", -1);
        
        if (groupId != -1) {
            currentGroup = databaseHelper.getGroup(groupId);
            if (currentGroup != null) {
                etGroupName.setText(currentGroup.getGroupName());
                etGroupDescription.setText(currentGroup.getDescription());
            }
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveGroup());
        btnDelete.setOnClickListener(v -> deleteGroup());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveGroup() {
        String groupName = etGroupName.getText().toString().trim();
        String groupDescription = etGroupDescription.getText().toString().trim();

        if (groupName.isEmpty()) {
            etGroupName.setError("Group name is required");
            etGroupName.requestFocus();
            return;
        }

        if (currentGroup == null) {
            // Create new group
            Group newGroup = new Group();
            newGroup.setGroupName(groupName);
            newGroup.setDescription(groupDescription.isEmpty() ? null : groupDescription);
            
            long groupId = databaseHelper.addGroup(newGroup);
            if (groupId != -1) {
                Toast.makeText(this, "Group created successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("group_created", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing group
            currentGroup.setGroupName(groupName);
            currentGroup.setDescription(groupDescription.isEmpty() ? null : groupDescription);
            
            boolean updated = databaseHelper.updateGroup(currentGroup);
            if (updated) {
                Toast.makeText(this, "Group updated successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("group_updated", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Failed to update group", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteGroup() {
        if (currentGroup != null) {
            // Show confirmation dialog
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Group")
                .setMessage("Are you sure you want to delete this group? This will also delete all expenses and members.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteGroup(groupId);
                    if (deleted) {
                        Toast.makeText(this, "Group deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("group_deleted", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete group", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        }
    }
}
