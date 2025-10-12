package com.example.expensetracker.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.expensetracker.models.Expense;
import com.example.expensetracker.models.Member;

import java.text.DecimalFormat;
import java.util.List;

public class SMSHelper {
    private static final String TAG = "SMSHelper";
    private static final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    
    private Context context;
    private SmsManager smsManager;

    public SMSHelper(Context context) {
        this.context = context;
        this.smsManager = SmsManager.getDefault();
    }

    public boolean hasSMSPermission() {
        boolean hasPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) 
                == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "SMS Permission granted: " + hasPermission);
        return hasPermission;
    }

    public void sendInvoiceSMS(Expense expense, List<Member> members) {
        if (!hasSMSPermission()) {
            Log.e(TAG, "SMS permission not granted");
            return;
        }

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

            // Find member with phone number
            Member member = findMemberByName(members, participant);
            if (member != null && member.getPhoneNumber() != null && !member.getPhoneNumber().isEmpty()) {
                String message = createInvoiceMessage(expense, participant, amount);
                sendSMS(member.getPhoneNumber(), message);
            }
        }
    }

    private Member findMemberByName(List<Member> members, String name) {
        for (Member member : members) {
            if (member.getMemberName().equals(name)) {
                return member;
            }
        }
        return null;
    }

    private String createInvoiceMessage(Expense expense, String participant, double amount) {
        StringBuilder message = new StringBuilder();
        message.append("Expense Invoice\n\n");
        message.append("Expense: ").append(expense.getExpenseName()).append("\n");
        message.append("Total Amount: $").append(String.format("%.2f", expense.getAmount())).append("\n");
        message.append("Your Share: $").append(String.format("%.2f", amount)).append("\n");
        message.append("Date: ").append(expense.getDate()).append("\n");
        
        if (expense.getLocation() != null && !expense.getLocation().isEmpty()) {
            message.append("Location: ").append(expense.getLocation()).append("\n");
        }
        
        if (expense.getDescription() != null && !expense.getDescription().isEmpty()) {
            message.append("Description: ").append(expense.getDescription()).append("\n");
        }
        
        message.append("\nPlease pay your share to ").append(expense.getPayer()).append(".");
        
        return message.toString();
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            Log.d(TAG, "=== ATTEMPTING TO SEND SMS ===");
            Log.d(TAG, "Original phone number: " + phoneNumber);
            Log.d(TAG, "Message length: " + message.length());
            Log.d(TAG, "Message preview: " + message.substring(0, Math.min(50, message.length())) + "...");
            
            // Clean and validate phone number
            String cleanPhoneNumber = cleanPhoneNumber(phoneNumber);
            if (cleanPhoneNumber == null || cleanPhoneNumber.isEmpty()) {
                Log.e(TAG, "Invalid phone number after cleaning: " + phoneNumber);
                return;
            }
            
            // Validate message
            if (message == null || message.trim().isEmpty()) {
                Log.e(TAG, "Message is null or empty");
                return;
            }
            
            // Check if message is too long (SMS limit is usually 160 characters)
            if (message.length() > 160) {
                Log.w(TAG, "Message is longer than 160 characters, might be split into multiple SMS");
            }
            
            Log.d(TAG, "Sending SMS to: " + cleanPhoneNumber);
            Log.d(TAG, "Full message: " + message);
            
            // Try different SMS sending approaches
            try {
                // Method 1: Standard SMS sending
                smsManager.sendTextMessage(cleanPhoneNumber, null, message, null, null);
                Log.d(TAG, "SMS sent successfully to " + cleanPhoneNumber);
                
                // Add a small delay to prevent rapid SMS sending
                Thread.sleep(100);
                
            } catch (Exception e) {
                Log.e(TAG, "Standard SMS failed, trying alternative method: " + e.getMessage());
                
                // Method 2: Try with delivery report
                try {
                    smsManager.sendTextMessage(cleanPhoneNumber, null, message, null, null);
                    Log.d(TAG, "SMS sent with alternative method to " + cleanPhoneNumber);
                } catch (Exception e2) {
                    Log.e(TAG, "Alternative SMS method also failed: " + e2.getMessage());
                    throw e2;
                }
            }
            
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception - SMS permission not granted: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS to " + phoneNumber + ": " + e.getMessage(), e);
        }
    }
    
    private String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            Log.e(TAG, "Phone number is null or empty");
            return null;
        }
        
        // Just trim whitespace and return as entered by user
        String cleaned = phoneNumber.trim();
        Log.d(TAG, "Original phone number: " + phoneNumber);
        Log.d(TAG, "Cleaned phone number: " + cleaned);
        
        // Basic validation - should have some digits
        String digitsOnly = cleaned.replaceAll("[^\\d]", "");
        if (digitsOnly.length() < 5) {
            Log.e(TAG, "Phone number too short: " + phoneNumber + " -> " + cleaned + " (digits: " + digitsOnly.length() + ")");
            return null;
        }
        
        Log.d(TAG, "Final phone number (as entered by user): " + cleaned);
        return cleaned;
    }

    public void sendBalanceReminder(List<Member> members) {
        if (!hasSMSPermission()) {
            Log.e(TAG, "SMS permission not granted");
            return;
        }

        Log.d(TAG, "=== SENDING BALANCE REMINDERS ===");
        Log.d(TAG, "Number of members: " + members.size());

        for (Member member : members) {
            Log.d(TAG, "Processing member: " + member.getMemberName());
            Log.d(TAG, "Phone number: " + member.getPhoneNumber());
            Log.d(TAG, "Balance: " + member.getBalance());
            
            if (member.getPhoneNumber() != null && !member.getPhoneNumber().trim().isEmpty()) {
                String message = createBalanceReminderMessage(member);
                Log.d(TAG, "Created message for " + member.getMemberName() + ": " + message);
                sendSMS(member.getPhoneNumber(), message);
            } else {
                Log.w(TAG, "No phone number for member: " + member.getMemberName());
            }
        }
        
        Log.d(TAG, "=== BALANCE REMINDERS COMPLETED ===");
    }

    private String createBalanceReminderMessage(Member member) {
        StringBuilder message = new StringBuilder();
        message.append("Balance Reminder\n\n");
        message.append("Hello ").append(member.getMemberName()).append(",\n\n");
        
        Log.d(TAG, "Creating message for member: " + member.getMemberName() + " with balance: " + member.getBalance());
        
        if (Math.abs(member.getBalance()) < 0.01) {
            // Balance is essentially zero (settled up)
            message.append("Your balance is settled up!\n");
            message.append("No payment needed at this time.");
        } else if (member.getBalance() > 0.01) {
            // Member owes money
            message.append("You owe: $").append(String.format("%.2f", member.getBalance())).append("\n");
            message.append("Please settle your balance soon.");
        } else if (member.getBalance() < -0.01) {
            // Member is owed money
            message.append("You are owed: $").append(String.format("%.2f", Math.abs(member.getBalance()))).append("\n");
            message.append("You should receive payment soon.");
        }
        
        String finalMessage = message.toString();
        Log.d(TAG, "Final message for " + member.getMemberName() + ": " + finalMessage);
        return finalMessage;
    }
    
    // Test method to send a simple SMS for debugging
    public void sendTestSMS(String phoneNumber) {
        if (!hasSMSPermission()) {
            Log.e(TAG, "SMS permission not granted for test SMS");
            return;
        }
        
        String testMessage = "Test SMS from Expense Tracker app. If you receive this, SMS is working!";
        Log.d(TAG, "Sending test SMS to: " + phoneNumber);
        sendSMS(phoneNumber, testMessage);
    }
    
    // Method to send a very simple SMS (no emojis, minimal text)
    public void sendSimpleSMS(String phoneNumber, String simpleMessage) {
        if (!hasSMSPermission()) {
            Log.e(TAG, "SMS permission not granted for simple SMS");
            return;
        }
        
        Log.d(TAG, "Sending simple SMS to: " + phoneNumber + " Message: " + simpleMessage);
        sendSMS(phoneNumber, simpleMessage);
    }
    
    // Method to check if SMS service is available
    public boolean isSMSAvailable() {
        try {
            // Try to get SMS manager
            SmsManager smsManager = SmsManager.getDefault();
            return smsManager != null;
        } catch (Exception e) {
            Log.e(TAG, "SMS service not available: " + e.getMessage());
            return false;
        }
    }
}
