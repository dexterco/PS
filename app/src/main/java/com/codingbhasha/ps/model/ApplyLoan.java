package com.codingbhasha.ps.model;

public class ApplyLoan {


    String docId;
    String uniqueId;
    String accNum;
    String IFSC;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    String email;
    int amount;
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public ApplyLoan() {
    }

    public ApplyLoan(String uniqueId, String accNum, String IFSC, int amount) {
        this.uniqueId = uniqueId;
        this.accNum = accNum;
        this.IFSC = IFSC;
        this.amount = amount;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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
}
