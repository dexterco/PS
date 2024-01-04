package com.codingbhasha.ps.model;

public class AdminHome {

    String memberId;
    String plan;
    String dateOfJoining;
    int totalIds;
    int validity;
    int walletBalance;
    int Refund;



    public int getRefund() {
        return Refund;
    }

    public void setRefund(int refund) {
        Refund = refund;
    }

    public String getActivel1IDs() {
        return activel1IDs;
    }

    public void setActivel1IDs(String activel1IDs) {
        this.activel1IDs = activel1IDs;
    }

    public String getActivel2IDs() {
        return activel2IDs;
    }

    public void setActivel2IDs(String activel2IDs) {
        this.activel2IDs = activel2IDs;
    }

    public String getActivel3IDs() {
        return activel3IDs;
    }

    public void setActivel3IDs(String activel3IDs) {
        this.activel3IDs = activel3IDs;
    }

    public String getActivel4IDs() {
        return activel4IDs;
    }

    public void setActivel4IDs(String activel4IDs) {
        this.activel4IDs = activel4IDs;
    }

    public String getDactl1IDs() {
        return dactl1IDs;
    }

    public void setDactl1IDs(String dactl1IDs) {
        this.dactl1IDs = dactl1IDs;
    }

    public String getDactl2IDs() {
        return dactl2IDs;
    }

    public void setDactl2IDs(String dactl2IDs) {
        this.dactl2IDs = dactl2IDs;
    }

    public String getDactl3IDs() {
        return dactl3IDs;
    }

    public void setDactl3IDs(String dactl3IDs) {
        this.dactl3IDs = dactl3IDs;
    }

    public String getDactl4IDs() {
        return dactl4IDs;
    }

    public void setDactl4IDs(String dactl4IDs) {
        this.dactl4IDs = dactl4IDs;
    }

    String activel1IDs,
                        activel2IDs,
                        activel3IDs
                        ,activel4IDs

                        ,dactl1IDs
                        ,dactl2IDs
                        ,dactl3IDs
                        ,dactl4IDs;

    public AdminHome() {
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

    public void setWalletBalance(int walletBalance) {
        this.walletBalance = walletBalance;
    }
}
