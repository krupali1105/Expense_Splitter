package com.example.expensetracker.models;

public class Settlement {
    private String fromMember;
    private String toMember;
    private double amount;
    private int fromMemberId;
    private int toMemberId;
    private boolean isSettled;
    private String settlementDate;

    public Settlement() {
        this.isSettled = false;
    }

    public Settlement(String fromMember, String toMember, double amount) {
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.amount = amount;
        this.isSettled = false;
    }

    // Getters and Setters
    public String getFromMember() {
        return fromMember;
    }

    public void setFromMember(String fromMember) {
        this.fromMember = fromMember;
    }

    public String getToMember() {
        return toMember;
    }

    public void setToMember(String toMember) {
        this.toMember = toMember;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getFromMemberId() {
        return fromMemberId;
    }

    public void setFromMemberId(int fromMemberId) {
        this.fromMemberId = fromMemberId;
    }

    public int getToMemberId() {
        return toMemberId;
    }

    public void setToMemberId(int toMemberId) {
        this.toMemberId = toMemberId;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    public String getSettlementDescription() {
        return fromMember + " should pay " + toMember + " " + getFormattedAmount();
    }
}
