package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.adapters.GroupAdapter;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Group;
import com.example.expensetracker.utils.NotificationHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements GroupAdapter.OnGroupClickListener {
    private RecyclerView recyclerViewGroups;
    private LinearLayout emptyStateLayout;
    private TextView tvTotalBalance;
    private TextView tvGroupCount;
    private FloatingActionButton fabAddGroup;
    private GroupAdapter groupAdapter;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private List<Group> groups;
    private NotificationHelper notificationHelper;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupToolbar();
        initializeDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadGroups();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Refresh data when returning to this activity
        loadGroups();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save any pending data
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
        recyclerViewGroups = findViewById(R.id.recyclerViewGroups);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvGroupCount = findViewById(R.id.tvGroupCount);
        fabAddGroup = findViewById(R.id.fabAddGroup);
        toolbar = findViewById(R.id.toolbar);
        sharedPreferences = getSharedPreferences("expense_tracker_prefs", MODE_PRIVATE);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
        notificationHelper = new NotificationHelper(this);
        
        // Create sample data if this is the first time
        if (sharedPreferences.getBoolean("first_time", true)) {
            createSampleData();
            sharedPreferences.edit().putBoolean("first_time", false).apply();
        }
    }

    private void setupRecyclerView() {
        groups = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, groups);
        groupAdapter.setOnGroupClickListener(this);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGroups.setAdapter(groupAdapter);
    }

    private void setupClickListeners() {
        fabAddGroup.setOnClickListener(v -> showAddGroupDialog());
    }

    private void loadGroups() {
        groups.clear();
        groups.addAll(databaseHelper.getAllGroups());
        groupAdapter.updateGroups(groups);
        
        // Update UI based on data
        if (groups.isEmpty()) {
            recyclerViewGroups.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            tvTotalBalance.setText("Total Balance: $0.00");
            tvGroupCount.setText("0 groups");
        } else {
            recyclerViewGroups.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            updateTotalBalance();
            tvGroupCount.setText(groups.size() + " groups");
        }
    }

    private void updateTotalBalance() {
        double totalBalance = 0;
        for (Group group : groups) {
            totalBalance += group.getTotalExpenses();
        }
        tvTotalBalance.setText(String.format("Total Balance: $%.2f", totalBalance));
    }

    private void showAddGroupDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_group, null);
        builder.setView(dialogView);
        
        final com.google.android.material.textfield.TextInputEditText etGroupName = 
            dialogView.findViewById(R.id.etGroupName);
        final com.google.android.material.textfield.TextInputEditText etGroupDescription = 
            dialogView.findViewById(R.id.etGroupDescription);
        final com.google.android.material.button.MaterialButton btnCancel = 
            dialogView.findViewById(R.id.btnCancel);
        final com.google.android.material.button.MaterialButton btnCreate = 
            dialogView.findViewById(R.id.btnCreate);
        
        android.app.AlertDialog dialog = builder.create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnCreate.setOnClickListener(v -> {
            String groupName = etGroupName.getText().toString().trim();
            String groupDescription = etGroupDescription.getText().toString().trim();
            
            if (!groupName.isEmpty()) {
                addNewGroup(groupName, groupDescription);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }

    private void addNewGroup(String groupName, String groupDescription) {
        Group newGroup = new Group();
        newGroup.setGroupName(groupName);
        newGroup.setDescription(groupDescription.isEmpty() ? 
            "Group created on " + new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date()) : 
            groupDescription);
        newGroup.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        
        long groupId = databaseHelper.addGroup(newGroup);
        if (groupId != -1) {
            Toast.makeText(this, "Group added successfully", Toast.LENGTH_SHORT).show();
            notificationHelper.showGroupCreatedNotification(groupName);
            loadGroups();
        } else {
            Toast.makeText(this, "Failed to add group", Toast.LENGTH_SHORT).show();
        }
    }

    private void createSampleData() {
        // Create a sample group
        Group sampleGroup = new Group();
        sampleGroup.setGroupName("Trip to Paris");
        sampleGroup.setDescription("Weekend trip with friends");
        sampleGroup.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        databaseHelper.addGroup(sampleGroup);
        
        // Add some sample members
        com.example.expensetracker.models.Member member1 = new com.example.expensetracker.models.Member();
        member1.setGroupId(1);
        member1.setMemberName("Alice");
        member1.setEmail("alice@example.com");
        databaseHelper.addMember(member1);
        
        com.example.expensetracker.models.Member member2 = new com.example.expensetracker.models.Member();
        member2.setGroupId(1);
        member2.setMemberName("Bob");
        member2.setEmail("bob@example.com");
        databaseHelper.addMember(member2);
        
        com.example.expensetracker.models.Member member3 = new com.example.expensetracker.models.Member();
        member3.setGroupId(1);
        member3.setMemberName("Charlie");
        member3.setEmail("charlie@example.com");
        databaseHelper.addMember(member3);
    }

    @Override
    public void onGroupClick(Group group) {
        Intent intent = new Intent(this, GroupDetailsActivity.class);
        intent.putExtra("group_id", group.getGroupId());
        intent.putExtra("group_name", group.getGroupName());
        intent.putExtra("group_description", group.getDescription());
        startActivity(intent);
    }

    @Override
    public void onEditGroup(Group group) {
        Intent intent = new Intent(this, EditGroupActivity.class);
        intent.putExtra("group_id", group.getGroupId());
        intent.putExtra("group_name", group.getGroupName());
        intent.putExtra("group_description", group.getDescription());
        startActivityForResult(intent, 1001);
    }

    @Override
    public void onDeleteGroup(Group group) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Group")
                .setMessage("Are you sure you want to delete \"" + group.getGroupName() + "\"? This will also delete all expenses and members in this group.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteGroup(group.getGroupId());
                    if (deleted) {
                        Toast.makeText(this, "Group deleted successfully", Toast.LENGTH_SHORT).show();
                        loadGroups();
                    } else {
                        Toast.makeText(this, "Failed to delete group", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Group was updated, refresh the list
            loadGroups();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}