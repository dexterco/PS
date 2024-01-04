package com.codingbhasha.ps.model;

public class UserProfile {
String planAID,planBID,planCID;


    public UserProfile(String planAID, String planBID, String planCID, String email, String name, String phone, String lastLogin) {
        this.planAID = planAID;
        this.planBID = planBID;
        this.planCID = planCID;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.lastLogin = lastLogin;
    }

    public String getPlanAID() {
        return planAID;
    }

    public void setPlanAID(String planAID) {
        this.planAID = planAID;
    }

    public String getPlanBID() {
        return planBID;
    }

    public void setPlanBID(String planBID) {
        this.planBID = planBID;
    }

    public String getPlanCID() {
        return planCID;
    }

    public void setPlanCID(String planCID) {
        this.planCID = planCID;
    }

    String email;
    String name;
    String phone;
    String lastLogin;

    public UserProfile() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
}
