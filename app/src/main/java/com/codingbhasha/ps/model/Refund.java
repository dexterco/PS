package com.codingbhasha.ps.model;

public class Refund {
   long date;
    String id ;
   int refund;

    public Refund() {

    }

    public Refund(long date, String id, int refund) {
        this.date = date;
        this.id = id;
        this.refund = refund;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRefund() {
        return refund;
    }

    public void setRefund(int refund) {
        this.refund = refund;
    }
}
