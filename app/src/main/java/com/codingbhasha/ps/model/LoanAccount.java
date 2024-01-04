package com.codingbhasha.ps.model;

public class LoanAccount {

    String accNum;
    String IFSC;
    String loanId;
    long date;
    String status;



    String MemberID;
    public int loanAmt;
    public int recoveredAmt;
    public int outstandingAmt;
    public String getMemberID() {
        return MemberID;
    }

    public void setMemberID(String memberID) {
        MemberID = memberID;
    }
    public LoanAccount() {
    }

    public LoanAccount(String accNum, String IFSC, String loanId, long date, String status, int loanAmt, int recoveredAmt, int outstandingAmt) {
        this.accNum = accNum;
        this.IFSC = IFSC;
        this.loanId = loanId;
        this.date = date;
        this.status = status;
        this.loanAmt = loanAmt;
        this.recoveredAmt = recoveredAmt;
        this.outstandingAmt = outstandingAmt;
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

    public long getEnd_date() {
        return end_date;
    }

    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }

    long end_date ;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLoanAmt() {
        return loanAmt;
    }

    public void setLoanAmt(int loanAmt) {
        this.loanAmt = loanAmt;
    }

    public int getRecoveredAmt() {
        return recoveredAmt;
    }

    public void setRecoveredAmt(int recoveredAmt) {
        this.recoveredAmt = recoveredAmt;
    }

    public int getOutstandingAmt() {
        return outstandingAmt;
    }

    public void setOutstandingAmt(int outstandingAmt) {
        this.outstandingAmt = outstandingAmt;
    }
}
