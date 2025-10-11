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
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) 
                == PackageManager.PERMISSION_GRANTED;
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
        message.append("💰 Expense Invoice\n\n");
        message.append("Expense: ").append(expense.getExpenseName()).append("\n");
        message.append("Total Amount: ").append(currencyFormat.format(expense.getAmount())).append("\n");
        message.append("Your Share: ").append(currencyFormat.format(amount)).append("\n");
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
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d(TAG, "SMS sent to " + phoneNumber);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS to " + phoneNumber, e);
        }
    }

    public void sendBalanceReminder(List<Member> members) {
        if (!hasSMSPermission()) {
            Log.e(TAG, "SMS permission not granted");
            return;
        }

        for (Member member : members) {
            if (member.getPhoneNumber() != null && !member.getPhoneNumber().isEmpty()) {
                String message = createBalanceReminderMessage(member);
                sendSMS(member.getPhoneNumber(), message);
            }
        }
    }

    private String createBalanceReminderMessage(Member member) {
        StringBuilder message = new StringBuilder();
        message.append("📊 Balance Reminder\n\n");
        message.append("Hello ").append(member.getMemberName()).append(",\n\n");
        
        if (member.getBalance() > 0) {
            message.append("You owe: ").append(currencyFormat.format(member.getBalance())).append("\n");
            message.append("Please settle your balance soon.");
        } else if (member.getBalance() < 0) {
            message.append("You are owed: ").append(currencyFormat.format(Math.abs(member.getBalance()))).append("\n");
            message.append("You should receive payment soon.");
        } else {
            message.append("Your balance is settled up! 🎉");
        }
        
        return message.toString();
    }
}
