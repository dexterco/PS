package com.codingbhasha.ps.model;

public class MonthlyIncome {

    public int l1IDs;
    public int l2IDs;
    public int l3IDs;
    public int l4IDs;
    public int l1Amt;
    public int l2Amt;
    public int l3Amt;
    public int l4Amt;
    public int total;
    String month;
    long date;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MonthlyIncome() {
    }

    public MonthlyIncome(int l1IDs, int l2IDs, String id, int l3IDs, int l4IDs, int l1Amt, int l2Amt, int l3Amt, int l4Amt, int total, String month, long date) {
        this.l1IDs = l1IDs;
        this.l2IDs = l2IDs;
        this.l3IDs = l3IDs;
        this.l4IDs = l4IDs;
        this.l1Amt = l1Amt;
        this.l2Amt = l2Amt;
        this.l3Amt = l3Amt;
        this.l4Amt = l4Amt;
        this.total = total;
        this.month = month;
        this.date = date;
        this.id = id;
    }

    public int getL1IDs() {
        return l1IDs;
    }

    public void setL1IDs(int l1IDs) {
        this.l1IDs = l1IDs;
    }

    public int getL2IDs() {
        return l2IDs;
    }

    public void setL2IDs(int l2IDs) {
        this.l2IDs = l2IDs;
    }

    public int getL3IDs() {
        return l3IDs;
    }

    public void setL3IDs(int l3IDs) {
        this.l3IDs = l3IDs;
    }

    public int getL4IDs() {
        return l4IDs;
    }

    public void setL4IDs(int l4IDs) {
        this.l4IDs = l4IDs;
    }

    public int getL1Amt() {
        return l1Amt;
    }

    public void setL1Amt(int l1Amt) {
        this.l1Amt = l1Amt;
    }

    public int getL2Amt() {
        return l2Amt;
    }

    public void setL2Amt(int l2Amt) {
        this.l2Amt = l2Amt;
    }

    public int getL3Amt() {
        return l3Amt;
    }

    public void setL3Amt(int l3Amt) {
        this.l3Amt = l3Amt;
    }

    public int getL4Amt() {
        return l4Amt;
    }

    public void setL4Amt(int l4Amt) {
        this.l4Amt = l4Amt;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
