package com.evan.parknbark.settings.admin;

class UserItem {
    private String displayName;
    private String email;
    private String uid;
    private boolean banned;

    UserItem(String displayName, String email, String uid, boolean banned) {
        setDisplayName(displayName);
        setEmail(email);
        setUid(uid);
        setBanned(banned);
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
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
