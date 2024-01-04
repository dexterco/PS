package com.codingbhasha.ps.model;

public class MonthlyBonus {

    String month;
    String rank;
    public int l1IDs;
    public int l2IDs;
    public int l3IDs;
    public int l4IDs;
    public int bonus;

    String  id ;long date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public MonthlyBonus() {
    }

    public MonthlyBonus(String month, String rank, int l1IDs, int l2IDs, int l3IDs, int l4IDs, int bonus,String  id ,long date) {
        this.month = month;
        this.rank = rank;
        this.l1IDs = l1IDs;
        this.l2IDs = l2IDs;
        this.l3IDs = l3IDs;
        this.l4IDs = l4IDs;
        this.bonus = bonus;
        this.id =  id;
        this.date =  date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
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

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
