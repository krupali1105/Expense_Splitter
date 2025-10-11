package com.example.expensetracker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String DISPLAY_FORMAT = "MMM dd, yyyy";
    public static final String DATABASE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "MMM dd, yyyy HH:mm";

    public static String formatDateForDisplay(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault());
        return formatter.format(date);
    }

    public static String formatDateForDatabase(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATABASE_FORMAT, Locale.getDefault());
        return formatter.format(date);
    }

    public static String formatDateTimeForDisplay(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
        return formatter.format(date);
    }

    public static String getCurrentDateForDatabase() {
        return formatDateForDatabase(new Date());
    }

    public static String getCurrentDateForDisplay() {
        return formatDateForDisplay(new Date());
    }
}
