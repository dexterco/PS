package com.codingbhasha.ps.model;

public class LienOutstandingAmt {

    public int amount;
    String loanId;

    public String getMemberid() {
        return Memberid;
    }

    public void setMemberid(String memberid) {
        Memberid = memberid;
    }

    long date;
    String Memberid;

    public LienOutstandingAmt() {
    }

    public LienOutstandingAmt(int amount, String loanId, long date) {
        this.amount = amount;
        this.loanId = loanId;
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
