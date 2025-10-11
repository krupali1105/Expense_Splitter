package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.expensetracker.models.Expense;
import com.example.expensetracker.models.Group;
import com.example.expensetracker.models.Member;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 3;

    // Table names
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_EXPENSES = "expenses";
    private static final String TABLE_MEMBERS = "members";

    // Groups table columns
    private static final String COLUMN_GROUP_ID = "group_id";
    private static final String COLUMN_GROUP_NAME = "group_name";
    private static final String COLUMN_GROUP_DESCRIPTION = "description";
    private static final String COLUMN_CREATED_DATE = "created_date";

    // Expenses table columns
    private static final String COLUMN_EXPENSE_ID = "expense_id";
    private static final String COLUMN_EXPENSE_NAME = "expense_name";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_PAYER = "payer";
    private static final String COLUMN_PARTICIPANTS = "participants";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PARTICIPANT_AMOUNTS = "participant_amounts";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    // Members table columns
    private static final String COLUMN_MEMBER_ID = "member_id";
    private static final String COLUMN_MEMBER_NAME = "member_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_TOTAL_OWED = "total_owed";
    private static final String COLUMN_TOTAL_OWING = "total_owing";
    private static final String COLUMN_BALANCE = "balance";

    // Create table statements
    private static final String CREATE_TABLE_GROUPS = "CREATE TABLE " + TABLE_GROUPS + "("
            + COLUMN_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_GROUP_NAME + " TEXT NOT NULL,"
            + COLUMN_GROUP_DESCRIPTION + " TEXT,"
            + COLUMN_CREATED_DATE + " TEXT"
            + ")";

    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE " + TABLE_EXPENSES + "("
            + COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_GROUP_ID + " INTEGER NOT NULL,"
            + COLUMN_EXPENSE_NAME + " TEXT NOT NULL,"
            + COLUMN_AMOUNT + " REAL NOT NULL,"
            + COLUMN_PAYER + " TEXT NOT NULL,"
            + COLUMN_PARTICIPANTS + " TEXT NOT NULL,"
            + COLUMN_PARTICIPANT_AMOUNTS + " TEXT,"
            + COLUMN_DATE + " TEXT NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_CATEGORY + " TEXT,"
            + COLUMN_LOCATION + " TEXT,"
            + COLUMN_LATITUDE + " REAL,"
            + COLUMN_LONGITUDE + " REAL,"
            + "FOREIGN KEY(" + COLUMN_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + ")"
            + ")";

    private static final String CREATE_TABLE_MEMBERS = "CREATE TABLE " + TABLE_MEMBERS + "("
            + COLUMN_MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_GROUP_ID + " INTEGER NOT NULL,"
            + COLUMN_MEMBER_NAME + " TEXT NOT NULL,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PHONE_NUMBER + " TEXT,"
            + COLUMN_TOTAL_OWED + " REAL DEFAULT 0,"
            + COLUMN_TOTAL_OWING + " REAL DEFAULT 0,"
            + COLUMN_BALANCE + " REAL DEFAULT 0,"
            + "FOREIGN KEY(" + COLUMN_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GROUPS);
        db.execSQL(CREATE_TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_MEMBERS);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add new columns to expenses table
            db.execSQL("ALTER TABLE " + TABLE_EXPENSES + " ADD COLUMN " + COLUMN_PARTICIPANT_AMOUNTS + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_EXPENSES + " ADD COLUMN " + COLUMN_LOCATION + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_EXPENSES + " ADD COLUMN " + COLUMN_LATITUDE + " REAL");
            db.execSQL("ALTER TABLE " + TABLE_EXPENSES + " ADD COLUMN " + COLUMN_LONGITUDE + " REAL");
        }
        if (oldVersion < 3) {
            // Add phone number column to members table
            db.execSQL("ALTER TABLE " + TABLE_MEMBERS + " ADD COLUMN " + COLUMN_PHONE_NUMBER + " TEXT");
        }
    }

    // Group CRUD operations
    public long addGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, group.getGroupName());
        values.put(COLUMN_GROUP_DESCRIPTION, group.getDescription());
        values.put(COLUMN_CREATED_DATE, group.getCreatedDate());

        long id = db.insert(TABLE_GROUPS, null, values);
        db.close();
        return id;
    }

    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GROUPS + " ORDER BY " + COLUMN_CREATED_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Group group = new Group();
                group.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID)));
                group.setGroupName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME)));
                group.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_DESCRIPTION)));
                group.setCreatedDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_DATE)));

                // Calculate total expenses for this group
                double totalExpenses = getTotalExpensesForGroup(group.getGroupId());
                group.setTotalExpenses(totalExpenses);

                // Calculate member count for this group
                int memberCount = getMemberCountForGroup(group.getGroupId());
                group.setMemberCount(memberCount);

                groups.add(group);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return groups;
    }

    public Group getGroup(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GROUPS, null, COLUMN_GROUP_ID + "=?",
                new String[]{String.valueOf(groupId)}, null, null, null);

        Group group = null;
        if (cursor.moveToFirst()) {
            group = new Group();
            group.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID)));
            group.setGroupName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME)));
            group.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_DESCRIPTION)));
            group.setCreatedDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_DATE)));
        }
        cursor.close();
        db.close();
        return group;
    }

    public boolean updateGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, group.getGroupName());
        values.put(COLUMN_GROUP_DESCRIPTION, group.getDescription());

        int result = db.update(TABLE_GROUPS, values, COLUMN_GROUP_ID + "=?",
                new String[]{String.valueOf(group.getGroupId())});
        db.close();
        return result > 0;
    }

    public boolean deleteGroup(int groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete related expenses and members first
        db.delete(TABLE_EXPENSES, COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)});
        db.delete(TABLE_MEMBERS, COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)});
        int result = db.delete(TABLE_GROUPS, COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)});
        db.close();
        return result > 0;
    }

    // Expense CRUD operations
    public long addExpense(Expense expense) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_GROUP_ID, expense.getGroupId());
            values.put(COLUMN_EXPENSE_NAME, expense.getExpenseName());
            values.put(COLUMN_AMOUNT, expense.getAmount());
            values.put(COLUMN_PAYER, expense.getPayer());
            values.put(COLUMN_PARTICIPANTS, expense.getParticipants());
            values.put(COLUMN_PARTICIPANT_AMOUNTS, expense.getParticipantAmounts());
            values.put(COLUMN_DATE, expense.getDate());
            values.put(COLUMN_DESCRIPTION, expense.getDescription());
            values.put(COLUMN_CATEGORY, expense.getCategory());
            values.put(COLUMN_LOCATION, expense.getLocation());
            values.put(COLUMN_LATITUDE, expense.getLatitude());
            values.put(COLUMN_LONGITUDE, expense.getLongitude());

            long id = db.insert(TABLE_EXPENSES, null, values);
            db.close();
            
            // Update member balances after adding expense - with error handling
            try {
                recalculateAllBalancesForGroup(expense.getGroupId());
            } catch (Exception e) {
                e.printStackTrace();
                // Don't fail the expense addition if balance update fails
            }
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Expense> getExpensesForGroup(int groupId) {
        List<Expense> expenses = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_GROUP_ID + "=? ORDER BY " + COLUMN_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setExpenseId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)));
                expense.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID)));
                expense.setExpenseName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_NAME)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));
                expense.setPayer(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYER)));
                expense.setParticipants(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARTICIPANTS)));
                expense.setParticipantAmounts(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARTICIPANT_AMOUNTS)));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                expense.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                expense.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)));
                expense.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPENSE_NAME, expense.getExpenseName());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_PAYER, expense.getPayer());
        values.put(COLUMN_PARTICIPANTS, expense.getParticipants());
        values.put(COLUMN_PARTICIPANT_AMOUNTS, expense.getParticipantAmounts());
        values.put(COLUMN_DATE, expense.getDate());
        values.put(COLUMN_DESCRIPTION, expense.getDescription());
        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_LOCATION, expense.getLocation());
        values.put(COLUMN_LATITUDE, expense.getLatitude());
        values.put(COLUMN_LONGITUDE, expense.getLongitude());

        int result = db.update(TABLE_EXPENSES, values, COLUMN_EXPENSE_ID + "=?",
                new String[]{String.valueOf(expense.getExpenseId())});
        db.close();
        
        // Update member balances after updating expense
        updateMemberBalances(expense.getGroupId());
        return result;
    }

    public void deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Get group ID before deleting
        Cursor cursor = db.query(TABLE_EXPENSES, new String[]{COLUMN_GROUP_ID}, COLUMN_EXPENSE_ID + "=?",
                new String[]{String.valueOf(expenseId)}, null, null, null);
        int groupId = -1;
        if (cursor.moveToFirst()) {
            groupId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID));
        }
        cursor.close();

        db.delete(TABLE_EXPENSES, COLUMN_EXPENSE_ID + "=?", new String[]{String.valueOf(expenseId)});
        db.close();
        
        // Update member balances after deleting expense
        if (groupId != -1) {
            updateMemberBalances(groupId);
        }
    }

    // Member CRUD operations
    public long addMember(Member member) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ID, member.getGroupId());
        values.put(COLUMN_MEMBER_NAME, member.getMemberName());
        values.put(COLUMN_EMAIL, member.getEmail());
        values.put(COLUMN_PHONE_NUMBER, member.getPhoneNumber());
        values.put(COLUMN_TOTAL_OWED, member.getTotalOwed());
        values.put(COLUMN_TOTAL_OWING, member.getTotalOwing());
        values.put(COLUMN_BALANCE, member.getBalance());

        long id = db.insert(TABLE_MEMBERS, null, values);
        db.close();
        return id;
    }

    public boolean updateMember(Member member) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEMBER_NAME, member.getMemberName());
        values.put(COLUMN_EMAIL, member.getEmail());
        values.put(COLUMN_PHONE_NUMBER, member.getPhoneNumber());
        values.put(COLUMN_TOTAL_OWED, member.getTotalOwed());
        values.put(COLUMN_TOTAL_OWING, member.getTotalOwing());
        values.put(COLUMN_BALANCE, member.getBalance());

        int result = db.update(TABLE_MEMBERS, values, COLUMN_MEMBER_ID + "=?",
                new String[]{String.valueOf(member.getMemberId())});
        db.close();
        return result > 0;
    }

    public boolean deleteMember(int memberId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_MEMBERS, COLUMN_MEMBER_ID + "=?", new String[]{String.valueOf(memberId)});
        db.close();
        return result > 0;
    }

    public List<Member> getMembersForGroup(int groupId) {
        List<Member> members = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEMBERS + " WHERE " + COLUMN_GROUP_ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                Member member = new Member();
                member.setMemberId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEMBER_ID)));
                member.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID)));
                member.setMemberName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEMBER_NAME)));
                member.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                member.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)));
                member.setTotalOwed(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_OWED)));
                member.setTotalOwing(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_OWING)));
                member.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BALANCE)));
                members.add(member);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return members;
    }

    // Helper methods
    private double getTotalExpensesForGroup(int groupId) {
        String selectQuery = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_GROUP_ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(groupId)});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    private int getMemberCountForGroup(int groupId) {
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_MEMBERS + " WHERE " + COLUMN_GROUP_ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(groupId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    private void updateMemberBalances(int groupId) {
        try {
            Log.d(TAG, "Starting balance calculation for group: " + groupId);
            SQLiteDatabase db = this.getWritableDatabase();
            
            // Reset all balances to 0
            ContentValues resetValues = new ContentValues();
            resetValues.put(COLUMN_TOTAL_OWED, 0);
            resetValues.put(COLUMN_TOTAL_OWING, 0);
            resetValues.put(COLUMN_BALANCE, 0);
            int resetCount = db.update(TABLE_MEMBERS, resetValues, COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)});
            Log.d(TAG, "Reset " + resetCount + " members to 0 balance");
            
            // Get all expenses for this group
            List<Expense> expenses = getExpensesForGroup(groupId);
            Log.d(TAG, "Found " + (expenses != null ? expenses.size() : 0) + " expenses");
            
            if (expenses == null || expenses.isEmpty()) {
                db.close();
                return;
            }
            
            // Calculate balances based on expenses
            for (Expense expense : expenses) {
                if (expense == null || expense.getParticipants() == null || expense.getParticipants().isEmpty()) {
                    continue;
                }
                
                Log.d(TAG, "Processing expense: " + expense.getExpenseName() + " Amount: " + expense.getAmount());
                Log.d(TAG, "Participants: " + expense.getParticipants());
                Log.d(TAG, "Payer: " + expense.getPayer());
                
                String[] participants = expense.getParticipants().split(", ");
                String[] participantAmounts = null;
                
                // Check if custom amounts are available
                if (expense.getParticipantAmounts() != null && !expense.getParticipantAmounts().isEmpty()) {
                    participantAmounts = expense.getParticipantAmounts().split(", ");
                    Log.d(TAG, "Custom amounts: " + expense.getParticipantAmounts());
                }
                
                // Calculate amounts for each participant
                for (int i = 0; i < participants.length; i++) {
                    String participant = participants[i].trim();
                    if (participant.isEmpty()) {
                        continue;
                    }
                    
                    double amount;
                    
                    if (participantAmounts != null && i < participantAmounts.length) {
                        // Use custom amount
                        try {
                            amount = Double.parseDouble(participantAmounts[i].trim());
                        } catch (NumberFormatException e) {
                            // Fallback to equal split if custom amount is invalid
                            amount = expense.getAmount() / participants.length;
                        }
                    } else {
                        // Equal split
                        amount = expense.getAmount() / participants.length;
                    }
                    
                    Log.d(TAG, "Participant: " + participant + " Amount: " + amount);
                    
                    if (participant.equals(expense.getPayer())) {
                        // Payer gets money back (total amount - their share)
                        double payerGetsBack = expense.getAmount() - amount;
                        Log.d(TAG, "Payer " + participant + " gets back: " + payerGetsBack);
                        
                        // Get current total_owing and add to it
                        Cursor cursor = db.query(TABLE_MEMBERS, new String[]{COLUMN_TOTAL_OWING}, 
                                COLUMN_GROUP_ID + "=? AND " + COLUMN_MEMBER_NAME + "=?", 
                                new String[]{String.valueOf(groupId), participant}, null, null, null);
                        double currentOwing = 0;
                        if (cursor.moveToFirst()) {
                            currentOwing = cursor.getDouble(0);
                        }
                        cursor.close();
                        
                        ContentValues payerValues = new ContentValues();
                        payerValues.put(COLUMN_TOTAL_OWING, currentOwing + payerGetsBack);
                        int updateCount = db.update(TABLE_MEMBERS, payerValues, COLUMN_GROUP_ID + "=? AND " + COLUMN_MEMBER_NAME + "=?",
                                new String[]{String.valueOf(groupId), participant});
                        Log.d(TAG, "Updated payer " + participant + " total_owing to: " + (currentOwing + payerGetsBack) + " (rows affected: " + updateCount + ")");
                    } else {
                        // Other participants owe money
                        Log.d(TAG, "Participant " + participant + " owes: " + amount);
                        
                        // Get current total_owed and add to it
                        Cursor cursor = db.query(TABLE_MEMBERS, new String[]{COLUMN_TOTAL_OWED}, 
                                COLUMN_GROUP_ID + "=? AND " + COLUMN_MEMBER_NAME + "=?", 
                                new String[]{String.valueOf(groupId), participant}, null, null, null);
                        double currentOwed = 0;
                        if (cursor.moveToFirst()) {
                            currentOwed = cursor.getDouble(0);
                        }
                        cursor.close();
                        
                        ContentValues participantValues = new ContentValues();
                        participantValues.put(COLUMN_TOTAL_OWED, currentOwed + amount);
                        int updateCount = db.update(TABLE_MEMBERS, participantValues, COLUMN_GROUP_ID + "=? AND " + COLUMN_MEMBER_NAME + "=?",
                                new String[]{String.valueOf(groupId), participant});
                        Log.d(TAG, "Updated participant " + participant + " total_owed to: " + (currentOwed + amount) + " (rows affected: " + updateCount + ")");
                    }
                }
            }
            
            // Calculate final balances
            String updateBalanceQuery = "UPDATE " + TABLE_MEMBERS + " SET " + COLUMN_BALANCE + " = " + COLUMN_TOTAL_OWED + " - " + COLUMN_TOTAL_OWING + " WHERE " + COLUMN_GROUP_ID + "=?";
            db.execSQL(updateBalanceQuery, new String[]{String.valueOf(groupId)});
            
            // Log final balances
            Cursor finalCursor = db.query(TABLE_MEMBERS, new String[]{COLUMN_MEMBER_NAME, COLUMN_TOTAL_OWED, COLUMN_TOTAL_OWING, COLUMN_BALANCE}, 
                    COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)}, null, null, null);
            while (finalCursor.moveToNext()) {
                String memberName = finalCursor.getString(0);
                double totalOwed = finalCursor.getDouble(1);
                double totalOwing = finalCursor.getDouble(2);
                double balance = finalCursor.getDouble(3);
                Log.d(TAG, "Final balance for " + memberName + ": owed=" + totalOwed + ", owing=" + totalOwing + ", balance=" + balance);
            }
            finalCursor.close();
            
            db.close();
            Log.d(TAG, "Balance calculation completed for group: " + groupId);
        } catch (Exception e) {
            Log.e(TAG, "Error in balance calculation: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // FIXED: Simple and correct balance calculation method
    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Delete all data from all tables
            db.delete(TABLE_EXPENSES, null, null);
            db.delete(TABLE_MEMBERS, null, null);
            db.delete(TABLE_GROUPS, null, null);
            Log.d(TAG, "All data cleared successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void recalculateAllBalancesForGroup(int groupId) {
        try {
            Log.d(TAG, "=== STARTING BALANCE RECALCULATION FOR GROUP " + groupId + " ===");
            SQLiteDatabase db = this.getWritableDatabase();
            
            // First, let's check if we have any members in this group
            Cursor memberCheck = db.query(TABLE_MEMBERS, new String[]{COLUMN_MEMBER_ID, COLUMN_MEMBER_NAME}, 
                    COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)}, null, null, null);
            Log.d(TAG, "Found " + memberCheck.getCount() + " members in group " + groupId);
            memberCheck.close();

            // 1. Reset all balances to 0
            ContentValues resetValues = new ContentValues();
            resetValues.put(COLUMN_TOTAL_OWED, 0);
            resetValues.put(COLUMN_TOTAL_OWING, 0);
            resetValues.put(COLUMN_BALANCE, 0);
            int resetCount = db.update(TABLE_MEMBERS, resetValues, COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)});
            Log.d(TAG, "Reset " + resetCount + " members to 0 balance");

            // 2. Get all expenses for this group
            List<Expense> expenses = getExpensesForGroup(groupId);
            Log.d(TAG, "Found " + (expenses != null ? expenses.size() : 0) + " expenses");

            if (expenses == null || expenses.isEmpty()) {
                Log.d(TAG, "No expenses found, balances remain at 0");
                db.close();
                return;
            }

            // 3. Process each expense
            for (Expense expense : expenses) {
                if (expense == null || expense.getParticipants() == null || expense.getParticipants().isEmpty()) {
                    continue;
                }

                Log.d(TAG, "Processing expense: " + expense.getExpenseName() + " Amount: " + expense.getAmount());
                Log.d(TAG, "Payer: " + expense.getPayer());
                Log.d(TAG, "Participants: " + expense.getParticipants());

                String[] participants = expense.getParticipants().split(", ");
                String[] participantAmounts = null;

                // Check if custom amounts are available
                if (expense.getParticipantAmounts() != null && !expense.getParticipantAmounts().isEmpty()) {
                    participantAmounts = expense.getParticipantAmounts().split(", ");
                    Log.d(TAG, "Custom amounts: " + expense.getParticipantAmounts());
                }

                // Process each participant
                for (int i = 0; i < participants.length; i++) {
                    String participant = participants[i].trim();
                    if (participant.isEmpty()) {
                        continue;
                    }

                    // Calculate participant's share
                    double share;
                    if (participantAmounts != null && i < participantAmounts.length) {
                        try {
                            share = Double.parseDouble(participantAmounts[i].trim());
                        } catch (NumberFormatException e) {
                            share = expense.getAmount() / participants.length;
                        }
                    } else {
                        share = expense.getAmount() / participants.length;
                    }

                    Log.d(TAG, "Participant: " + participant + " Share: " + share);

                    // Find member by name (case-insensitive and trim whitespace)
                    Cursor memberCursor = db.query(TABLE_MEMBERS, new String[]{COLUMN_MEMBER_ID, COLUMN_MEMBER_NAME, COLUMN_TOTAL_OWED, COLUMN_TOTAL_OWING}, 
                            COLUMN_GROUP_ID + "=? AND LOWER(TRIM(" + COLUMN_MEMBER_NAME + ")) = LOWER(TRIM(?))", 
                            new String[]{String.valueOf(groupId), participant}, null, null, null);
                    
                    if (!memberCursor.moveToFirst()) {
                        Log.e(TAG, "Member not found: " + participant);
                        memberCursor.close();
                        continue;
                    }
                    
                    int memberId = memberCursor.getInt(0);
                    String memberName = memberCursor.getString(1);
                    double currentOwed = memberCursor.getDouble(2);
                    double currentOwing = memberCursor.getDouble(3);
                    memberCursor.close();
                    
                    Log.d(TAG, "Found member: " + memberName + " (ID: " + memberId + ")");

                    if (participant.equals(expense.getPayer())) {
                        // Payer gets money back (total amount - their share)
                        double payerGetsBack = expense.getAmount() - share;
                        Log.d(TAG, "Payer " + participant + " gets back: " + payerGetsBack);

                        // Update payer's total_owing (money they should get back)
                        ContentValues payerValues = new ContentValues();
                        payerValues.put(COLUMN_TOTAL_OWING, currentOwing + payerGetsBack);
                        int updateCount = db.update(TABLE_MEMBERS, payerValues, 
                                COLUMN_MEMBER_ID + "=?", 
                                new String[]{String.valueOf(memberId)});
                        Log.d(TAG, "Updated payer " + participant + " total_owing to: " + (currentOwing + payerGetsBack) + " (rows: " + updateCount + ")");
                    } else {
                        // Other participants owe their share
                        Log.d(TAG, "Participant " + participant + " owes: " + share);

                        // Update participant's total_owed (money they owe)
                        ContentValues participantValues = new ContentValues();
                        participantValues.put(COLUMN_TOTAL_OWED, currentOwed + share);
                        int updateCount = db.update(TABLE_MEMBERS, participantValues, 
                                COLUMN_MEMBER_ID + "=?", 
                                new String[]{String.valueOf(memberId)});
                        Log.d(TAG, "Updated participant " + participant + " total_owed to: " + (currentOwed + share) + " (rows: " + updateCount + ")");
                    }
                }
            }

            // 4. Calculate final balances (total_owed - total_owing)
            String updateBalanceQuery = "UPDATE " + TABLE_MEMBERS + " SET " + COLUMN_BALANCE + " = " + COLUMN_TOTAL_OWED + " - " + COLUMN_TOTAL_OWING + " WHERE " + COLUMN_GROUP_ID + "=?";
            db.execSQL(updateBalanceQuery, new String[]{String.valueOf(groupId)});

            // 5. Log final results
            Cursor finalCursor = db.query(TABLE_MEMBERS, 
                    new String[]{COLUMN_MEMBER_NAME, COLUMN_TOTAL_OWED, COLUMN_TOTAL_OWING, COLUMN_BALANCE}, 
                    COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)}, null, null, null);
            while (finalCursor.moveToNext()) {
                String memberName = finalCursor.getString(0);
                double totalOwed = finalCursor.getDouble(1);
                double totalOwing = finalCursor.getDouble(2);
                double balance = finalCursor.getDouble(3);
                Log.d(TAG, "FINAL: " + memberName + " -> owed=" + totalOwed + ", owing=" + totalOwing + ", balance=" + balance);
            }
            finalCursor.close();

            db.close();
            Log.d(TAG, "=== BALANCE RECALCULATION COMPLETED ===");
        } catch (Exception e) {
            Log.e(TAG, "Error in balance recalculation: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // New simplified balance calculation method
    public void recalculateBalances(int groupId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            
            // Reset all balances to 0
            ContentValues resetValues = new ContentValues();
            resetValues.put(COLUMN_TOTAL_OWED, 0);
            resetValues.put(COLUMN_TOTAL_OWING, 0);
            resetValues.put(COLUMN_BALANCE, 0);
            db.update(TABLE_MEMBERS, resetValues, COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)});
            
            // Get all expenses for this group
            List<Expense> expenses = getExpensesForGroup(groupId);
            
            if (expenses == null || expenses.isEmpty()) {
                db.close();
                return;
            }
            
            // Calculate balances based on expenses
            for (Expense expense : expenses) {
                if (expense == null || expense.getParticipants() == null || expense.getParticipants().isEmpty()) {
                    continue;
                }
                
                String[] participants = expense.getParticipants().split(", ");
                String[] participantAmounts = null;
                
                // Check if custom amounts are available
                if (expense.getParticipantAmounts() != null && !expense.getParticipantAmounts().isEmpty()) {
                    participantAmounts = expense.getParticipantAmounts().split(", ");
                }
                
                // Calculate amounts for each participant
                for (int i = 0; i < participants.length; i++) {
                    String participant = participants[i].trim();
                    if (participant.isEmpty()) {
                        continue;
                    }
                    
                    double amount;
                    
                    if (participantAmounts != null && i < participantAmounts.length) {
                        // Use custom amount
                        try {
                            amount = Double.parseDouble(participantAmounts[i].trim());
                        } catch (NumberFormatException e) {
                            // Fallback to equal split if custom amount is invalid
                            amount = expense.getAmount() / participants.length;
                        }
                    } else {
                        // Equal split
                        amount = expense.getAmount() / participants.length;
                    }
                    
                    if (participant.equals(expense.getPayer())) {
                        // Payer gets money back (total amount - their share)
                        // Get current total_owing and add to it
                        Cursor cursor = db.query(TABLE_MEMBERS, new String[]{COLUMN_TOTAL_OWING}, 
                                COLUMN_GROUP_ID + "=? AND " + COLUMN_MEMBER_NAME + "=?", 
                                new String[]{String.valueOf(groupId), participant}, null, null, null);
                        double currentOwing = 0;
                        if (cursor.moveToFirst()) {
                            currentOwing = cursor.getDouble(0);
                        }
                        cursor.close();
                        
                        ContentValues payerValues = new ContentValues();
                        payerValues.put(COLUMN_TOTAL_OWING, currentOwing + (expense.getAmount() - amount));
                        db.update(TABLE_MEMBERS, payerValues, COLUMN_GROUP_ID + "=? AND " + COLUMN_MEMBER_NAME + "=?",
                                new String[]{String.valueOf(groupId), participant});
                    } else {
                        // Other participants owe money
                        // Get current total_owed and add to it
                        Cursor cursor = db.query(TABLE_MEMBERS, new String[]{COLUMN_TOTAL_OWED}, 
                                COLUMN_GROUP_ID + "=? AND " + COLUMN_MEMBER_NAME + "=?", 
                                new String[]{String.valueOf(groupId), participant}, null, null, null);
                        double currentOwed = 0;
                        if (cursor.moveToFirst()) {
                            currentOwed = cursor.getDouble(0);
                        }
                        cursor.close();
                        
                        ContentValues participantValues = new ContentValues();
                        participantValues.put(COLUMN_TOTAL_OWED, currentOwed + amount);
                        db.update(TABLE_MEMBERS, participantValues, COLUMN_GROUP_ID + "=? AND " + COLUMN_MEMBER_NAME + "=?",
                                new String[]{String.valueOf(groupId), participant});
                    }
                }
            }
            
            // Calculate final balances
            String updateBalanceQuery = "UPDATE " + TABLE_MEMBERS + " SET " + COLUMN_BALANCE + " = " + COLUMN_TOTAL_OWED + " - " + COLUMN_TOTAL_OWING + " WHERE " + COLUMN_GROUP_ID + "=?";
            db.execSQL(updateBalanceQuery, new String[]{String.valueOf(groupId)});
            
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            // If there's an error, just return without crashing
        }
    }
    
    // Test method to debug balance calculation
    public void debugBalanceCalculation(int groupId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            
            // Check members
            Cursor memberCursor = db.query(TABLE_MEMBERS, new String[]{COLUMN_MEMBER_ID, COLUMN_MEMBER_NAME, COLUMN_TOTAL_OWED, COLUMN_TOTAL_OWING, COLUMN_BALANCE}, 
                    COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)}, null, null, null);
            Log.d(TAG, "=== DEBUG: MEMBERS IN GROUP " + groupId + " ===");
            while (memberCursor.moveToNext()) {
                int id = memberCursor.getInt(0);
                String name = memberCursor.getString(1);
                double owed = memberCursor.getDouble(2);
                double owing = memberCursor.getDouble(3);
                double balance = memberCursor.getDouble(4);
                Log.d(TAG, "Member: " + name + " (ID:" + id + ") owed=" + owed + ", owing=" + owing + ", balance=" + balance);
            }
            memberCursor.close();
            
            // Check expenses
            Cursor expenseCursor = db.query(TABLE_EXPENSES, new String[]{COLUMN_EXPENSE_NAME, COLUMN_AMOUNT, COLUMN_PAYER, COLUMN_PARTICIPANTS, COLUMN_PARTICIPANT_AMOUNTS}, 
                    COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)}, null, null, null);
            Log.d(TAG, "=== DEBUG: EXPENSES IN GROUP " + groupId + " ===");
            while (expenseCursor.moveToNext()) {
                String name = expenseCursor.getString(0);
                double amount = expenseCursor.getDouble(1);
                String payer = expenseCursor.getString(2);
                String participants = expenseCursor.getString(3);
                String amounts = expenseCursor.getString(4);
                Log.d(TAG, "Expense: " + name + " Amount:" + amount + " Payer:" + payer + " Participants:" + participants + " Amounts:" + amounts);
            }
            expenseCursor.close();
            
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Debug error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
