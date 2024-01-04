package com.codingbhasha.ps.model;

public class ReferralIncome {

    int amount;
    long date;
    int levelNum;
    String idNumber;
    String userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ReferralIncome() {
    }

    public ReferralIncome(int amount, long date, int levelNum, String idNumber,String userID) {
        this.amount = amount;
        this.date = date;
        this.levelNum = levelNum;
        this.idNumber = idNumber;
        this.userID = userID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
