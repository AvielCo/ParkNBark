package com.evan.parknbark.settings.admin;

class UserItem {
    private String displayName;
    private String email;
    private String uid;

    UserItem(String displayName, String email, String uid) {
        setDisplayName(displayName);
        setEmail(email);
        setUid(uid);
    }

    String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) { this.uid = uid; }

    String getUid() { return uid; }

    String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
