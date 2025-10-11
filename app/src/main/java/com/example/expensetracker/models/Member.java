package com.example.expensetracker.models;

public class Member {
    private int memberId;
    private int groupId;
    private String memberName;
    private String email;
    private String phoneNumber;
    private double totalOwed;
    private double totalOwing;
    private double balance; // positive = owes money, negative = gets money back

    // Constructors
    public Member() {}

    public Member(int groupId, String memberName, String email) {
        this.groupId = groupId;
        this.memberName = memberName;
        this.email = email;
    }

    public Member(int memberId, int groupId, String memberName, String email, double totalOwed, double totalOwing, double balance) {
        this.memberId = memberId;
        this.groupId = groupId;
        this.memberName = memberName;
        this.email = email;
        this.totalOwed = totalOwed;
        this.totalOwing = totalOwing;
        this.balance = balance;
    }

    // Getters and Setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getTotalOwed() {
        return totalOwed;
    }

    public void setTotalOwed(double totalOwed) {
        this.totalOwed = totalOwed;
    }

    public double getTotalOwing() {
        return totalOwing;
    }

    public void setTotalOwing(double totalOwing) {
        this.totalOwing = totalOwing;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Helper method to get formatted balance string
    public String getFormattedBalance() {
        if (balance > 0) {
            return String.format("Owes $%.2f", balance);
        } else if (balance < 0) {
            return String.format("Gets back $%.2f", Math.abs(balance));
        } else {
            return "Settled up";
        }
    }
}
