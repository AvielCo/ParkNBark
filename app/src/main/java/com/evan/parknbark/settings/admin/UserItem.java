package com.evan.parknbark.settings.admin;

class UserItem {
    private String displayName;
    private String email;

    UserItem(String displayName, String email) {
        setDisplayName(displayName);
        setEmail(email);
    }

    String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
