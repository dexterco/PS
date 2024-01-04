package com.codingbhasha.ps.model;

public class LevelAchievementBonus {

    public int totalIds;
    public int bonus;
    long date;
    String id ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LevelAchievementBonus() {
    }

    public int getTotalIds() {
        return totalIds;
    }

    public void setTotalIds(int totalIds) {
        this.totalIds = totalIds;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
