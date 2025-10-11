package com.example.expensetracker.models;

public class Balance {
    private String memberName;
    private double amount;
    private String type; // "owes" or "gets_back"

    // Constructors
    public Balance() {}

    public Balance(String memberName, double amount, String type) {
        this.memberName = memberName;
        this.amount = amount;
        this.type = type;
    }

    // Getters and Setters
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Helper method to get formatted balance string
    public String getFormattedBalance() {
        if ("owes".equals(type)) {
            return String.format("Owes $%.2f", amount);
        } else {
            return String.format("Gets back $%.2f", amount);
        }
    }
}
