package com.example.expensetracker.models;

public class Group {
    private int groupId;
    private String groupName;
    private String description;
    private String createdDate;
    private double totalExpenses;
    private int memberCount;

    // Constructors
    public Group() {}

    public Group(String groupName, String description) {
        this.groupName = groupName;
        this.description = description;
    }

    public Group(int groupId, String groupName, String description, String createdDate) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.description = description;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
