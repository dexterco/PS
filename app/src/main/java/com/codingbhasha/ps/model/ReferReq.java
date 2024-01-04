package com.codingbhasha.ps.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReferReq {
    String referrerid;
    String id;

    public String getReferrerid() {
        return referrerid;
    }

    public void setReferrerid(String referrerid) {
        this.referrerid = referrerid;
    }

    String referrerEmail;
    String newUserName;
    String newUserEmail;
    String newUserPhone;
    String plan;
    String planAmt;
    String paymentMode;
    String transactionID;
    String onlinePaymentMode;
    long date;

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        Date dated = new Date();
        dated.setTime( date);
        return dateFormat.format(dated) ;
    }



    public void setDate(long date) {
        this.date = date;
    }

    public ReferReq() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferrerEmail() {
        return referrerEmail;
    }

    public void setReferrerEmail(String referrerEmail) {
        this.referrerEmail = referrerEmail;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    public String getNewUserEmail() {
        return newUserEmail;
    }

    public void setNewUserEmail(String newUserEmail) {
        this.newUserEmail = newUserEmail;
    }

    public String getNewUserPhone() {
        return newUserPhone;
    }

    public void setNewUserPhone(String newUserPhone) {
        this.newUserPhone = newUserPhone;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getPlanAmt() {
        return planAmt;
    }

    public void setPlanAmt(String planAmt) {
        this.planAmt = planAmt;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getOnlinePaymentMode() {
        return onlinePaymentMode;
    }

    public void setOnlinePaymentMode(String onlinePaymentMode) {
        this.onlinePaymentMode = onlinePaymentMode;
    }
}
