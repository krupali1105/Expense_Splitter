package com.example.expensetracker.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.expensetracker.HomeActivity;
import com.example.expensetracker.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "expense_tracker_channel";
    private static final String CHANNEL_NAME = "Expense Tracker Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for expense settlements and reminders";
    
    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showSettlementReminder(String groupName, String memberName, double amount) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Settlement Reminder")
            .setContentText(String.format("%s owes $%.2f in %s", memberName, amount, groupName))
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(String.format("Don't forget! %s still owes $%.2f in the group '%s'. " +
                    "Tap to open the app and settle up.", memberName, amount, groupName)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showExpenseAddedNotification(String groupName, String expenseName, double amount) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 1, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Expense Added")
            .setContentText(String.format("$%.2f added to %s", amount, groupName))
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(String.format("A new expense '%s' of $%.2f has been added to the group '%s'. " +
                    "Check your balances!", expenseName, amount, groupName)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showGroupCreatedNotification(String groupName) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 2, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Group Created")
            .setContentText(String.format("Group '%s' is ready for expenses", groupName))
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(String.format("Great! You've created a new group '%s'. " +
                    "Start adding expenses and track who owes what!", groupName)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showBalanceUpdateNotification(String groupName, String memberName, double balance) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 3, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title, content;
        if (balance > 0) {
            title = "Balance Update";
            content = String.format("%s now owes $%.2f in %s", memberName, balance, groupName);
        } else if (balance < 0) {
            title = "You're Owed Money!";
            content = String.format("You should receive $%.2f from %s in %s", Math.abs(balance), memberName, groupName);
        } else {
            title = "Balance Settled";
            content = String.format("%s is all settled up in %s", memberName, groupName);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(content + " Tap to view details."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showWeeklySummaryNotification(String groupName, double totalExpenses, int expenseCount) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 4, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Weekly Summary")
            .setContentText(String.format("%s: $%.2f in %d expenses", groupName, totalExpenses, expenseCount))
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(String.format("This week in '%s': %d expenses totaling $%.2f. " +
                    "Check your balances and settle up!", groupName, expenseCount, totalExpenses)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showPaymentReminderNotification(String groupName, String payerName, double amount) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 5, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Payment Reminder")
            .setContentText(String.format("Don't forget to pay %s $%.2f", payerName, amount))
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(String.format("Friendly reminder: You owe %s $%.2f in the group '%s'. " +
                    "Consider settling up soon!", payerName, amount, groupName)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
}
