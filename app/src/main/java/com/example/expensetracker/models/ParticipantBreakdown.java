package com.example.expensetracker.models;

public class ParticipantBreakdown {
    private String participantName;
    private double amountPaid;
    private double amountOwed;
    private double netBalance; // Positive if participant owes, negative if participant is owed

    public ParticipantBreakdown(String participantName, double amountPaid, double amountOwed, double netBalance) {
        this.participantName = participantName;
        this.amountPaid = amountPaid;
        this.amountOwed = amountOwed;
        this.netBalance = netBalance;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getAmountOwed() {
        return amountOwed;
    }

    public void setAmountOwed(double amountOwed) {
        this.amountOwed = amountOwed;
    }

    public double getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(double netBalance) {
        this.netBalance = netBalance;
    }
}
