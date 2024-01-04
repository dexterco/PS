package com.codingbhasha.ps.model;

public class IdStatus {

    String memberId;
    String plan;
    String dateOfJoining;
    int totalIds;
    int validity;
    long walletBalance;

    public IdStatus() {
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public int getTotalIds() {
        return totalIds;
    }

    public void setTotalIds(int totalIds) {
        this.totalIds = totalIds;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public long getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(long walletBalance) {
        this.walletBalance = walletBalance;
    }
}
