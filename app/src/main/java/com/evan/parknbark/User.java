package com.evan.parknbark;

import java.io.Serializable;

public class User implements Serializable {
    private String firstName;
    private String lastName;
    private String permission;
    private float appRate;

    public User(){
        //do not delete.
    }

    public User(String firstName, String lastName, String permission) {
        setFirstName(firstName);
        setLastName(lastName);
        setPermission(permission);
        setAppRate(0);
    }

    public float getAppRate() { return appRate; }

    public void setAppRate(float appRate) { this.appRate = appRate; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
