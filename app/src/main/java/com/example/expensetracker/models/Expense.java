package com.example.expensetracker.models;

import java.util.List;

public class Expense {
    private int expenseId;
    private int groupId;
    private String expenseName;
    private double amount;
    private String payer;
    private String participants; // JSON string of participant names
    private String participantAmounts; // JSON string of participant amounts
    private String date;
    private String description;
    private String category;
    private String location; // Location where expense was made
    private double latitude; // GPS latitude
    private double longitude; // GPS longitude

    // Constructors
    public Expense() {}

    public Expense(int groupId, String expenseName, double amount, String payer, String participants, String date) {
        this.groupId = groupId;
        this.expenseName = expenseName;
        this.amount = amount;
        this.payer = payer;
        this.participants = participants;
        this.date = date;
    }

    public Expense(int expenseId, int groupId, String expenseName, double amount, String payer, String participants, String date, String description, String category) {
        this.expenseId = expenseId;
        this.groupId = groupId;
        this.expenseName = expenseName;
        this.amount = amount;
        this.payer = payer;
        this.participants = participants;
        this.date = date;
        this.description = description;
        this.category = category;
    }

    // Getters and Setters
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getParticipantAmounts() {
        return participantAmounts;
    }

    public void setParticipantAmounts(String participantAmounts) {
        this.participantAmounts = participantAmounts;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
