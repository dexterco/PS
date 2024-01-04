package com.codingbhasha.ps.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PayputRequest {

    String userID;
    long date;
    String accNum;
    String IFSC;
    int amount;
    String plan;

    public String getDocid() {
        return Docid;
    }

    public void setDocid(String docid) {
        Docid = docid;
    }

    String Docid;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String email;
    public PayputRequest() {
    }

    public PayputRequest(String userID, long date, String accNum, String IFSC, int amount) {
        this.userID = userID;
        this.date = date;
        this.accNum = accNum;
        this.IFSC = IFSC;
        this.amount = amount;
    }

    public String getUniqueId() {
        return userID;
    }

    public void setUniqueId(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        Date dated = new Date();
        dated.setTime( date);
        return dateFormat.format(dated) ;
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

    public String getIFSC() {
        return IFSC;
    }

    public void setIFSC(String IFSC) {
        this.IFSC = IFSC;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }
}
