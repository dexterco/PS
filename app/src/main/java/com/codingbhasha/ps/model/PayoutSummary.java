package com.codingbhasha.ps.model;

public class PayoutSummary {

    public int amount;
    String transactionID;
    long date;
    String accNum;
    String userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public PayoutSummary() {
    }

    public PayoutSummary(int amount, String transactionID, long date ,String accNum,String userID) {
        this.amount = amount;
        this.transactionID = transactionID;
        this.date = date;
        this.accNum = accNum;
        this.userID = userID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAccNum() {
        return accNum;
    }

    public void setAccNum(String accNum) {
        this.accNum = accNum;
    }
}
