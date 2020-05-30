package com.evan.parknbark.utilities;

import java.io.Serializable;

public class User implements Serializable {
    private String emailAddress;
    private boolean isBanned;
    private String banReason;
    private String firstName;
    private String lastName;
    private String permission;
    private float appRate;
    private boolean builtProfile;

    public User() {
        //do not delete.
    }

    public User(String firstName, String lastName, String permission, String emailAddress) {
        setFirstName(firstName);
        setLastName(lastName);
        setPermission(permission);
        setAppRate(0);
        setEmailAddress(emailAddress);
        setBanned(false);
        setBanReason("");
        setBuiltProfile(false);
    }

    public boolean isBuiltProfile() {
        return builtProfile;
    }

    public void setBuiltProfile(boolean builtProfile) {
        this.builtProfile = builtProfile;
    }

    public float getAppRate() {
        return appRate;
    }

    public void setAppRate(float appRate) {
        this.appRate = appRate;
    }

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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }
}
